import { io, type Socket } from "socket.io-client";
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

let client: Socket | null = null;
const chatListeners = new Set<(event: ChatEvent) => void>();
const presenceListeners = new Set<(event: PresenceEvent) => void>();

function getWsUrl() {
  const override = import.meta.env.VITE_SOCKET_URL;
  if (override) return override;
  return API_BASE.replace(/:8080$/, ":8081");
}

export function connectChatSocket() {
  if (client && client.connected) return;

  const token = localStorage.getItem("auth_token");
  if (!token) return;

  client = io(getWsUrl(), {
    transports: ["websocket"],
    query: { token },
    reconnection: true,
    reconnectionDelay: 1000,
    reconnectionDelayMax: 5000,
  });

  client.on("connect", () => {
    console.log("[socket] connected", { id: client?.id, url: getWsUrl() });
  });

  client.on("disconnect", (reason) => {
    console.log("[socket] disconnected", { reason });
  });

  client.on("connect_error", (error) => {
    console.error("[socket] connect_error", error);
  });

  client.on("error", (error) => {
    console.error("[socket] error", error);
  });

  client.on("chat-update", (payload: ChatEvent) => {
    console.log("[socket] received chat-update", payload);
    chatListeners.forEach((listener) => listener(payload));
  });

  client.on("presence", (payload: PresenceEvent) => {
    console.log("[socket] received presence", payload);
    presenceListeners.forEach((listener) => listener(payload));
  });
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
  console.log("[socket] emit typing", { chatId, isTyping });
  client.emit("typing", { chatId, isTyping });
}

export function disconnectChatSocket() {
  if (!client) return;
  client.disconnect();
  client = null;
  chatListeners.clear();
  presenceListeners.clear();
}
