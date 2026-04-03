CREATE TABLE chat_reads (
                            id BIGSERIAL PRIMARY KEY,
                            chat_id BIGINT NOT NULL REFERENCES chats(id) ON DELETE CASCADE,
                            user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                            last_read_at TIMESTAMP NOT NULL DEFAULT NOW(),
                            CONSTRAINT unique_chat_read UNIQUE (chat_id, user_id)
);
