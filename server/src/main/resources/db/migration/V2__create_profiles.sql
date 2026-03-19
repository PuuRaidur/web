CREATE TABLE profiles (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                          display_name VARCHAR(100) NOT NULL,
                          about_me TEXT,
                          profile_picture_url TEXT,
                          location VARCHAR(100),
                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
