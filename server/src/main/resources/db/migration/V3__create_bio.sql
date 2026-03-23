CREATE TABLE bio (
                     id BIGSERIAL PRIMARY KEY,
                     user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                     hobbies TEXT,
                     music_preferences TEXT,
                     food_preferences TEXT,
                     interests TEXT,
                     looking_for TEXT,
                     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                     updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
