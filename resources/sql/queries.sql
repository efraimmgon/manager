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

-- :name get-all-projects :? :raw
-- :doc retrieve a user given the id.
SELECT * FROM projects;

-- :name create-project<! :<!
-- :doc creates a new project, returning its id
INSERT INTO projects p
(p.title, p.description)
VALUES (:title, :description)
RETURNING project_id;

-- :name get-project :? :1
-- :doc get project by project-id
SELECT * FROM projects p
WHERE p.project_id = :project-id;

-- :name update-project! :! :n
-- :doc update project by project-id
UPDATE projects
SET title = :title,
    description = :description,
    updated_at = (now() AT TIME ZONE 'utc')
WHERE project_id = :project-id;

-- :name delete-project! :! :n
-- :doc delete project by project-id
DELETE FROM projects
WHERE project_id = :project-id;

-- -----------------------------------------------------------------------------
-- Features
-- -----------------------------------------------------------------------------

-- :name get-features-by-project :? :raw
-- :doc get all features by project-id
SELECT * FROM features f
WHERE f.project_id = :project-id;
