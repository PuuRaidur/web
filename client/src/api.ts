const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8080";

function getHeaders(): Record<string, string> {
  const token = localStorage.getItem("token");
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  };
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  return headers;
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: { ...getHeaders(), ...(options?.headers || {}) },
  });

  if (!res.ok) {
    throw new Error(`API error: ${res.status} ${res.statusText}`);
  }

  const text = await res.text();
  return text ? (JSON.parse(text) as T) : (undefined as unknown as T);
}

// --- Auth ---

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  userId: number;
}

export async function login(body: LoginRequest): Promise<AuthResponse> {
  return request<AuthResponse>("/auth/login", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export interface RegisterRequest {
  email: string;
  password: string;
}

export async function register(body: RegisterRequest): Promise<AuthResponse> {
  return request<AuthResponse>("/auth/register", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

// --- User / Profile ---

export interface UserSummary {
  id: number;
  name: string | null;
  profilePictureUrl: string | null;
}

export async function getUserSummary(id: number): Promise<UserSummary> {
  return request<UserSummary>(`/users/${id}`);
}

export async function getUserSummaries(ids: number[]): Promise<UserSummary[]> {
  return Promise.all(ids.map((id) => getUserSummary(id)));
}

export interface ProfileResponse {
  userId: number;
  displayName: string | null;
  aboutMe: string | null;
  profilePictureUrl: string | null;
  location: string | null;
}

export async function getUserProfile(id: number): Promise<ProfileResponse> {
  return request<ProfileResponse>(`/users/${id}/profile`);
}

// --- Recommendations ---

export interface RecommendationsResponse {
  ids: number[];
}

export async function getRecommendations(): Promise<RecommendationsResponse> {
  return request<RecommendationsResponse>("/recommendations");
}

export async function dismissRecommendation(userId: number): Promise<void> {
  await request<void>(`/recommendations/dismiss/${userId}`, { method: "POST" });
}

// --- Connections ---

export interface ConnectionDetail {
  connectionId: number;
  otherUserId: number;
}

export interface ConnectionRequestDetail {
  id: number;
  senderId: number;
  createdAt: string;
}

export interface ConnectionsResponse {
  ids: number[];
}

export async function getConnections(): Promise<ConnectionDetail[]> {
  return request<ConnectionDetail[]>("/connections");
}

export async function sendConnectionRequest(receiverId: number): Promise<{ id: number }> {
  return request<{ id: number }>(`/connections/request?receiverId=${receiverId}`, {
    method: "POST",
  });
}

export async function getIncomingRequests(): Promise<ConnectionRequestDetail[]> {
  return request<ConnectionRequestDetail[]>("/connections/requests");
}

export async function acceptConnectionRequest(requestId: number): Promise<void> {
  await request<void>(`/connections/accept?requestId=${requestId}`, { method: "POST" });
}

export async function dismissConnectionRequest(requestId: number): Promise<void> {
  await request<void>(`/connections/dismiss?requestId=${requestId}`, { method: "POST" });
}

export async function disconnect(connectionId: number): Promise<void> {
  await request<void>(`/connections/${connectionId}`, { method: "DELETE" });
}
