-- Reset all data and reseed
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- Reusable bcrypt hash for "password123"
-- This hash was generated with bcrypt; all seeded users share it for simplicity.
DO $$
BEGIN
  -- nothing here; block kept for readability
END $$;

INSERT INTO users (email, password_hash, created_at)
SELECT
  'user' || g || '@example.com',
  '$2b$10$QeZqK8L0W1v7LQm8C1oV3e6Q7KjJ1wE1y1g9lW4Q6o3u7hVd2Yt6y',
  NOW() - (g || ' days')::interval
FROM generate_series(1, 100) AS g;

-- Profiles
INSERT INTO profiles (user_id, display_name, about_me, profile_picture_url, location, created_at, updated_at)
SELECT
  u.id,
  'User ' || u.id,
  'Hello! I am user ' || u.id || ' and I enjoy meeting new people.',
  NULL,
  (ARRAY['Tallinn','Tartu','Riga','Helsinki','Vilnius'])[(u.id % 5) + 1],
  NOW(),
  NOW()
FROM users u;

-- Bio data (5 required fields)
INSERT INTO bio (user_id, hobbies, music_preferences, food_preferences, interests, looking_for, created_at, updated_at)
SELECT
  u.id,
  (ARRAY['hiking','gaming','cooking','reading','photography'])[(u.id % 5) + 1],
  (ARRAY['rock','jazz','classical','pop','electronic'])[(u.id % 5) + 1],
  (ARRAY['pizza','sushi','tacos','pasta','salad'])[(u.id % 5) + 1],
  (ARRAY['tech','art','travel','sports','music'])[(u.id % 5) + 1],
  (ARRAY['friends','dating','activity partners','professional networking','chat buddies'])[(u.id % 5) + 1],
  NOW(),
  NOW()
FROM users u;
