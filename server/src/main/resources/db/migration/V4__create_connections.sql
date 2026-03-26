-- Connection requests table (Phase 6)
CREATE TABLE connection_requests (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    receiver_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Prevent duplicate requests
    CONSTRAINT unique_connection_request UNIQUE (sender_id, receiver_id)
);

-- Connections table (Phase 9)
CREATE TABLE connections (
    id BIGSERIAL PRIMARY KEY,
    user1_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user2_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Prevent duplicate connections
    CONSTRAINT unique_connection UNIQUE (user1_id, user2_id)
);
