import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import type { ChatMessage } from "../api/types";
import { API_BASE } from "../api/client";

export type ChatEvent = {
  type: "message" | "read";
  chatId: number;
  message?: ChatMessage | null;
};

let client: Client | null = null;
const listeners = new Set<(event: ChatEvent) => void>();

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
          listeners.forEach((listener) => listener(payload));
        } catch (err) {
          console.warn("Failed to parse chat event", err);
        }
      });
    },
  });

  client.activate();
}

export function addChatListener(listener: (event: ChatEvent) => void) {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

export function disconnectChatSocket() {
  if (!client) return;
  client.deactivate();
  client = null;
  listeners.clear();
}
