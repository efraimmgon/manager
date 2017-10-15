-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(id, first_name, last_name, email, pass)
VALUES (:id, :first_name, :last_name, :email, :pass)

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id


-- -----------------------------------------------------------------------------
-- Projects
-- -----------------------------------------------------------------------------

-- :name all-projects :?
-- :doc retrieve a user given the id.
SELECT * FROM projects;

-- :name create-project<! :<!
-- :doc creates a new project, returning its id
INSERT INTO projects
(title, description)
VALUES (:title, :description)
RETURNING project_id;
