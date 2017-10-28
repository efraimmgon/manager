CREATE TABLE status (
  status_id SERIAL PRIMARY KEY,
  name TEXT
);
--;;
INSERT INTO status (name) VALUES ('on progress');
--;;
INSERT INTO status (name) VALUES ('done');
--;;
INSERT INTO status (name) VALUES ('pending');
