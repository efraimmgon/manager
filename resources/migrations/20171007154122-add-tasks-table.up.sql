CREATE TABLE tasks (
  task_id     SERIAL PRIMARY KEY,
  title       TEXT,
  description TEXT,
  orig_est    INT,
  curr_est    INT,
  velocity    NUMERIC,
  story_id    INTEGER REFERENCES stories (story_id) ON DELETE CASCADE,
  status      TEXT,
  created_at  TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
  updated_at  TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc')
);
