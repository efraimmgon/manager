CREATE TABLE users
(user_id SERIAL PRIMARY KEY,
 first_name VARCHAR(30),
 last_name VARCHAR(30),
 email VARCHAR(30) UNIQUE,
 admin BOOLEAN,
 last_login TIMESTAMP,
 is_active BOOLEAN,
 created_at  TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
 updated_at  TIMESTAMP NOT NULL DEFAULT (now() AT TIME ZONE 'utc'),
 pass VARCHAR(300));
