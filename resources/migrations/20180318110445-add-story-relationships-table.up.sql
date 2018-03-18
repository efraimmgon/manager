CREATE TABLE story_relationships (
  story_relationship_id SERIAL PRIMARY KEY,
  story_id1 INT NOT NULL,
  story_id2 INT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
  updated_at TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc')
);
