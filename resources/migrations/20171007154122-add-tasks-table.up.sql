CREATE TABLE tasks (
  task_id     SERIAL PRIMARY KEY,
  title       TEXT,
  description TEXT,
  orig_est    INT,
  curr_est    INT,
  elapsed     INT,
  remain      INT,
  velocity    NUMERIC,
  feature_id  INT       NOT NULL REFERENCES features (feature_id),
  priority_id INT       NOT NULL REFERENCES priorities (priority_id),
  status_id   INT       NOT NULL REFERENCES status (status_id),
  created_at  TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
  updated_at  TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc')
);
