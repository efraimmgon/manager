-- :name create-user<! :<!
INSERT INTO users
(first_name, last_name, email, is_active, admin, pass)
VALUES (:first-name, :last-name, :email, :is-active, :admin, :pass)
RETURNING user_id;

-- :name get-user :? :1
-- :doc retrieve user by user-id
SELECT * FROM users
WHERE user_id = :user-id;

-- :name get-users :? :raw
-- :doc retrieve all users
SELECT * FROM users;

-- :name update-user! :! :n
-- :doc update user by user-id
UPDATE users
SET first_name = :first-name,
    last_name = :last-name,
    email = :email,
    admin = :admin,
    last_login = :last-login,
    is_active = :is-active,
    updated_at = (now() AT TIME ZONE 'utc'),
    pass = :pass
WHERE user_id = :user-id;

-- :name delete-user! :! :n
-- :doc delete user by user-id
DELETE FROM users
WHERE user_id = :user-id;
