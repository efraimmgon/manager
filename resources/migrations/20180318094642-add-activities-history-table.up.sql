CREATE TABLE activities_history (
  activity_history_id SERIAL PRIMARY KEY,
  user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
  verb TEXT,
  property TEXT,
  entity TEXT,
  entity_id INTEGER,
  changed_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
  old_value TEXT,
  new_value TEXT
);
