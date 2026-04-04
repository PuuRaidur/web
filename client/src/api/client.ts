import type {
  ConnectionRequestResponse,
  ConnectionResponse,
  MeResponse,
  ChatListItem,
  ChatMessage,
  ProfileResponse,
  BioResponse,
  RecommendationResponse,
  UserSummary,
} from "./types";

const API_BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

function getToken() {
  return localStorage.getItem("auth_token");
}

async function apiFetch<T>(path: string, init?: RequestInit): Promise<T> {
  const token = getToken();
  const headers = new Headers(init?.headers);

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  if (!headers.has("Content-Type") && init?.body) {
    headers.set("Content-Type", "application/json");
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...init,
    headers,
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed: ${response.status}`);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const contentType = response.headers.get("content-type") ?? "";
  if (!contentType.includes("application/json")) {
    return undefined as T;
  }

  return (await response.json()) as T;
}

async function apiPost(path: string, body?: unknown) {
  await apiFetch<void>(path, {
    method: "POST",
    body: body ? JSON.stringify(body) : undefined,
  });
}

export async function fetchRecommendations() {
  return apiFetch<RecommendationResponse>("/recommendations");
}

export async function fetchConnectionRequests() {
  return apiFetch<ConnectionRequestResponse>("/connections/requests");
}

export async function fetchOutgoingConnectionRequests() {
  return apiFetch<ConnectionRequestResponse>("/connections/requests/outgoing");
}

export async function fetchConnections() {
  return apiFetch<ConnectionResponse>("/connections");
}

export async function fetchUserSummary(id: number) {
  return apiFetch<UserSummary>(`/users/${id}`);
}

export async function fetchMe() {
  return apiFetch<MeResponse>("/me");
}

export async function fetchMyProfile() {
  return apiFetch<ProfileResponse>("/me/profile");
}

export async function fetchMyBio() {
  return apiFetch<BioResponse>("/me/bio");
}

export async function updateMyProfile(profile: {
  displayName: string;
  aboutMe: string;
  profilePictureUrl: string | null;
  location: string;
}) {
  return apiFetch<ProfileResponse>("/me/profile", {
    method: "PUT",
    body: JSON.stringify(profile),
  });
}

export async function updateMyBio(bio: {
  hobbies: string;
  musicPreferences: string;
  foodPreferences: string;
  interests: string;
  lookingFor: string;
}) {
  return apiFetch<BioResponse>("/me/bio", {
    method: "PUT",
    body: JSON.stringify(bio),
  });
}

export async function sendConnectionRequest(receiverId: number) {
  return apiPost("/connections/request", { receiverId });
}

export async function acceptConnectionRequest(senderId: number) {
  return apiPost("/connections/accept", { senderId });
}

export async function dismissConnectionRequest(senderId: number) {
  return apiPost("/connections/dismiss", { senderId });
}

export async function cancelConnectionRequest(receiverId: number) {
  return apiPost("/connections/cancel", { receiverId });
}

export async function login(email: string, password: string) {
  return apiFetch<{ token: string }>("/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

export async function register(email: string, password: string) {
  return apiFetch<{ token: string }>("/auth/register", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

export async function fetchChats() {
  return apiFetch<ChatListItem[]>("/chats");
}

export async function fetchChatMessages(chatId: number, page = 0, size = 20) {
  return apiFetch<ChatMessage[]>(
    `/chats/${chatId}/messages?page=${page}&size=${size}`
  );
}

export async function sendChatMessage(chatId: number, content: string) {
  return apiFetch<ChatMessage>(`/chats/${chatId}/messages`, {
    method: "POST",
    body: JSON.stringify({ content }),
  });
}

export async function markChatRead(chatId: number) {
  return apiPost(`/chats/${chatId}/read`);
}

export async function getOrCreateChat(otherUserId: number) {
  return apiFetch<{ chatId: number }>(`/chats/with?otherUserId=${otherUserId}`, {
    method: "POST",
  });
}
