-- Chats table: one chat per user pair
CREATE TABLE chats (
                       id BIGSERIAL PRIMARY KEY,
                       user1_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       user2_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       CONSTRAINT unique_chat_pair UNIQUE (user1_id, user2_id)
);

-- Messages table
CREATE TABLE messages (
                          id BIGSERIAL PRIMARY KEY,
                          chat_id BIGINT NOT NULL REFERENCES chats(id) ON DELETE CASCADE,
                          sender_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          content TEXT NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
