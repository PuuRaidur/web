import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import type { ChatMessage } from "../api/types";
import { API_BASE } from "../api/client";

export type ChatEvent = {
  type: "message" | "read" | "typing";
  chatId: number;
  senderId?: number | null;
  isTyping?: boolean | null;
  message?: ChatMessage | null;
};

export type PresenceEvent = {
  type: "presence";
  userId: number;
  online: boolean;
};

let client: Client | null = null;
const chatListeners = new Set<(event: ChatEvent) => void>();
const presenceListeners = new Set<(event: PresenceEvent) => void>();

function getWsUrl() {
  return `${API_BASE}/ws`;
}

export function connectChatSocket() {
  if (client && client.active) return;

  const token = localStorage.getItem("auth_token");
  if (!token) return;

  client = new Client({
    webSocketFactory: () => new SockJS(getWsUrl()),
    connectHeaders: {
      Authorization: `Bearer ${token}`,
    },
    reconnectDelay: 5000,
    onConnect: () => {
      client?.subscribe("/user/queue/chat-updates", (message) => {
        try {
          const payload = JSON.parse(message.body) as ChatEvent;
          chatListeners.forEach((listener) => listener(payload));
        } catch (err) {
          console.warn("Failed to parse chat event", err);
        }
      });

      client?.subscribe("/topic/presence", (message) => {
        try {
          const payload = JSON.parse(message.body) as PresenceEvent;
          presenceListeners.forEach((listener) => listener(payload));
        } catch (err) {
          console.warn("Failed to parse presence event", err);
        }
      });
    },
  });

  client.activate();
}

export function addChatListener(listener: (event: ChatEvent) => void) {
  chatListeners.add(listener);
  return () => chatListeners.delete(listener);
}

export function addPresenceListener(listener: (event: PresenceEvent) => void) {
  presenceListeners.add(listener);
  return () => presenceListeners.delete(listener);
}

export function sendTyping(chatId: number, isTyping: boolean) {
  if (!client || !client.connected) return;
  client.publish({
    destination: "/app/chat.typing",
    body: JSON.stringify({ chatId, isTyping }),
  });
}

export function disconnectChatSocket() {
  if (!client) return;
  client.deactivate();
  client = null;
  chatListeners.clear();
  presenceListeners.clear();
}
