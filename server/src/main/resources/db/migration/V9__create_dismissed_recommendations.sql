-- Dismissed recommendations table (Phase 4)
CREATE TABLE dismissed_recommendations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    dismissed_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Prevent duplicate dismissals
    CONSTRAINT unique_dismissed_recommendation UNIQUE (user_id, dismissed_user_id)
);

-- Index for faster lookups when filtering recommendations
CREATE INDEX idx_dismissed_recommendations_user_id ON dismissed_recommendations(user_id);
