CREATE TABLE stories (
  story_id  SERIAL PRIMARY KEY,
  title       TEXT,
  description TEXT,
  project_id  INTEGER REFERENCES projects (project_id),
  priority_id INT       NOT NULL REFERENCES priorities (priority_id),
  status_id   INT       NOT NULL REFERENCES status (status_id),
  type        INT       NOT NULL,
  created_at  TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
  updated_at  TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc')
);
