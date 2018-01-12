CREATE TABLE features (
  feature_id  SERIAL PRIMARY KEY,
  title       TEXT,
  description TEXT,
  project_id  INTEGER REFERENCES projects (project_id),
  priority_id INT       NOT NULL REFERENCES priorities (priority_id),
  created_at  TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
  updated_at  TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc')
);
