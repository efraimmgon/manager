CREATE TABLE priorities (
  priority_id SERIAL PRIMARY KEY,
  name TEXT
);
--;;
INSERT INTO priorities (name) VALUES ('1 - urgent');
--;;
INSERT INTO priorities (name) VALUES ('2 - high');
--;;
INSERT INTO priorities (name) VALUES ('3 - important');
--;;
INSERT INTO priorities (name) VALUES ('4 - medium');
--;;
INSERT INTO priorities (name) VALUES ('5 - moderate');
--;;
INSERT INTO priorities (name) VALUES ('6 - low');
--;;
INSERT INTO priorities (name) VALUES ('7 - don''t fix');
