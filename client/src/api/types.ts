export type RecommendationResponse = {
  ids: number[];
};

export type ConnectionRequestResponse = {
  ids: number[];
};

export type ConnectionDetail = {
  connectionId: number;
  otherUserId: number;
};

export type UserSummary = {
  id: number;
  name: string | null;
  profilePictureUrl: string | null;
  profileLink?: string | null;
};

export type MeResponse = {
  id: number;
  email: string;
  name: string | null;
  profilePictureUrl: string | null;
};

export type ProfileResponse = {
  userId: number;
  email: string | null;
  displayName: string | null;
  aboutMe: string | null;
  profilePictureUrl: string | null;
  location: string | null;
  preferredDistanceKm?: number | null;
  latitude?: number | null;
  longitude?: number | null;
};

export type BioResponse = {
  userId: number;
  hobbies: string | null;
  musicPreferences: string | null;
  foodPreferences: string | null;
  interests: string | null;
  lookingFor: string | null;
};

export type ChatListItem = {
  chatId: number;
  otherUserId: number;
  lastMessage: string | null;
  lastMessageAt: string | null;
  unreadCount: number;
};

export type ChatMessage = {
  id: number;
  chatId: number;
  senderId: number;
  content: string;
  createdAt: string;
};
