CREATE TABLE bios (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    
    -- Bio data points for recommendations
    interests TEXT[],           -- List of interests (e.g., ["technology", "music", "sports"])
    hobbies TEXT[],             -- List of hobbies (e.g., ["gaming", "reading", "cooking"])
    music_taste TEXT[],         -- Favorite music genres or artists
    age INTEGER,
    occupation VARCHAR(100),
    company VARCHAR(100),
    education VARCHAR(100),
    relationship_status VARCHAR(50),
    
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
