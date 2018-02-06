CREATE TABLE users_stories (
  users_stories_id SERIAL PRIMARY KEY,
  user_id    INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
  story_id   INTEGER REFERENCES stories (story_id) ON DELETE CASCADE,
  created_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
  updated_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc')
);
