import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useSearchParams } from "react-router-dom";
import {
  fetchChatMessages,
  fetchChats,
  fetchUserSummary,
  markChatRead,
  sendChatMessage,
} from "../api/client";
import type { ChatListItem, ChatMessage, UserSummary } from "../api/types";
import Avatar from "../components/Avatar";
import {
  addChatListener,
  addPresenceListener,
  connectChatSocket,
  sendTyping,
} from "../realtime/chatSocket";

function formatTimestamp(value: string | null) {
  if (!value) return "";
  const date = new Date(value);
  return new Intl.DateTimeFormat("en-US", {
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}

export default function Chats() {
  const [searchParams] = useSearchParams();
  const [chats, setChats] = useState<ChatListItem[]>([]);
  const [selectedChatId, setSelectedChatId] = useState<number | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [participants, setParticipants] = useState<Record<number, UserSummary>>(
    {}
  );
  const [presence, setPresence] = useState<Record<number, boolean>>({});
  const [typing, setTyping] = useState<Record<number, boolean>>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [draft, setDraft] = useState("");
  const typingTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

  const selectedChat = useMemo(
    () => chats.find((chat) => chat.chatId === selectedChatId) ?? null,
    [chats, selectedChatId]
  );

  const refreshChats = useCallback(async () => {
    try {
      setLoading(true);
      const data = await fetchChats();
      setChats(data);
      if (data.length > 0 && selectedChatId === null) {
        setSelectedChatId(data[0].chatId);
      }

      const uniqueUserIds = Array.from(new Set(data.map((c) => c.otherUserId)));
      const summaries = await Promise.all(uniqueUserIds.map(fetchUserSummary));
      const map: Record<number, UserSummary> = {};
      summaries.forEach((summary) => {
        map[summary.id] = summary;
      });
      setParticipants(map);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load chats");
    } finally {
      setLoading(false);
    }
  }, [selectedChatId]);

  useEffect(() => {
    refreshChats();
  }, [refreshChats]);

  useEffect(() => {
    const requestedChatId = searchParams.get("chatId");
    if (requestedChatId) {
      const nextId = Number(requestedChatId);
      if (!Number.isNaN(nextId) && nextId !== selectedChatId) {
        setSelectedChatId(nextId);
      }
    }
  }, [searchParams, selectedChatId]);

  useEffect(() => {
    let isActive = true;

    async function loadMessages() {
      if (!selectedChatId) return;

      try {
        const data = await fetchChatMessages(selectedChatId);
        if (isActive) {
          setMessages(data);
        }
        await markChatRead(selectedChatId);
      } catch (err) {
        if (isActive) {
          setError(err instanceof Error ? err.message : "Failed to load messages");
        }
      }
    }

    loadMessages();

    return () => {
      isActive = false;
    };
  }, [selectedChatId]);

  useEffect(() => {
    connectChatSocket();
    const unsubscribe = addChatListener((event) => {
      if (event.type === "message" && event.message) {
        if (event.chatId === selectedChatId) {
          setMessages((prev) => {
            if (prev.some((m) => m.id === event.message!.id)) {
              return prev;
            }
            return [event.message!, ...prev];
          });
        }
        refreshChats();
      }

      if (event.type === "typing") {
        if (!event.senderId) return;
        if (event.chatId !== selectedChatId) return;
        setTyping((prev) => ({
          ...prev,
          [event.chatId]: Boolean(event.isTyping),
        }));
      }

      if (event.type === "read") {
        refreshChats();
      }
    });

    const unsubscribePresence = addPresenceListener((event) => {
      setPresence((prev) => ({
        ...prev,
        [event.userId]: event.online,
      }));
    });

    return () => {
      unsubscribe();
      unsubscribePresence();
    };
  }, [refreshChats, selectedChatId]);

  async function handleSend() {
    if (!selectedChatId || !draft.trim()) return;

    try {
      const newMessage = await sendChatMessage(selectedChatId, draft.trim());
      setMessages((prev) => [newMessage, ...prev]);
      setDraft("");
      sendTyping(selectedChatId, false);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to send message");
    }
  }

  function handleDraftChange(nextValue: string) {
    setDraft(nextValue);
    if (!selectedChatId) return;
    sendTyping(selectedChatId, true);
    if (typingTimer.current) {
      clearTimeout(typingTimer.current);
    }
    typingTimer.current = setTimeout(() => {
      sendTyping(selectedChatId, false);
    }, 1500);
  }

  return (
    <section className="page chat-page">
      <header className="page-header">
        <div>
          <p className="eyebrow">Chat</p>
          <h1>Messages</h1>
          <p className="subtitle">Continue the conversations you started.</p>
        </div>
      </header>

      {loading && <p className="muted">Loading chats…</p>}
      {error && <p className="muted">{error}</p>}

      <div className="chat-layout">
        <aside className="chat-list">
          {chats.length === 0 && !loading && (
            <p className="muted">No chats yet.</p>
          )}
          {chats.map((chat) => {
            const user = participants[chat.otherUserId];
            return (
              <button
                key={chat.chatId}
                type="button"
                className={`chat-list-item ${
                  chat.chatId === selectedChatId ? "active" : ""
                }`}
                onClick={() => setSelectedChatId(chat.chatId)}
              >
                <Avatar
                  name={user?.name}
                  url={user?.profilePictureUrl}
                  status={
                    presence[chat.otherUserId] ? "online" : "offline"
                  }
                />
                <div className="chat-list-meta">
                  <div className="chat-list-title">
                    {user?.name ?? `User ${chat.otherUserId}`}
                  </div>
                  <div className="chat-list-sub">
                    {chat.lastMessage ?? "No messages yet"}
                  </div>
                </div>
                <div className="chat-list-side">
                  {chat.lastMessageAt && (
                    <span className="chat-list-time">
                      {formatTimestamp(chat.lastMessageAt)}
                    </span>
                  )}
                  {chat.unreadCount > 0 && (
                    <span className="chat-badge">{chat.unreadCount}</span>
                  )}
                </div>
              </button>
            );
          })}
        </aside>

        <section className="chat-panel">
          {selectedChat ? (
            <>
              <div className="chat-panel-header">
                <div className="chat-panel-title">
                  <h2>
                    {participants[selectedChat.otherUserId]?.name ??
                      `User ${selectedChat.otherUserId}`}
                  </h2>
                  <span className="muted">
                    {presence[selectedChat.otherUserId] ? "Online" : "Offline"}
                    {typing[selectedChat.chatId] ? " · Typing…" : ""}
                  </span>
                </div>
                <span className="muted">Chat ID {selectedChat.chatId}</span>
              </div>

              <div className="chat-messages">
                {messages.length === 0 ? (
                  <p className="muted">No messages yet.</p>
                ) : (
                  messages.map((message) => (
                    <div className="chat-message" key={message.id}>
                      <p>{message.content}</p>
                      <span className="muted">
                        {formatTimestamp(message.createdAt)}
                      </span>
                    </div>
                  ))
                )}
              </div>

              <div className="chat-input">
                <input
                  type="text"
                  value={draft}
                  onChange={(event) => handleDraftChange(event.target.value)}
                  placeholder="Write a message…"
                />
                <button className="primary-button" type="button" onClick={handleSend}>
                  Send
                </button>
              </div>
            </>
          ) : (
            <p className="muted">Select a chat to view messages.</p>
          )}
        </section>
      </div>
    </section>
  );
}
