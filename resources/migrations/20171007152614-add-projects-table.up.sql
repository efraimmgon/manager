CREATE TABLE projects (
  project_id  SERIAL PRIMARY KEY,
  title       TEXT,
  description TEXT,
  created_at  TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
  updated_at  TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc')
);
