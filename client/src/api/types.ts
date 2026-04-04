export type RecommendationResponse = {
  ids: number[];
};

export type ConnectionRequestResponse = {
  ids: number[];
};

export type ConnectionResponse = {
  ids: number[];
};

export type UserSummary = {
  id: number;
  name: string | null;
  profileImageUrl: string | null;
};

export type MeResponse = {
  id: number;
  name: string | null;
  profileImageUrl: string | null;
};

export type ProfileResponse = {
  userId: number;
  displayName: string | null;
  aboutMe: string | null;
  profilePictureUrl: string | null;
  location: string | null;
};

export type BioResponse = {
  userId: number;
  hobbies: string | null;
  musicPreferences: string | null;
  foodPreferences: string | null;
  interests: string | null;
  lookingFor: string | null;
};
