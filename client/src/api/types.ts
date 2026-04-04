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
