-- :name get-all-projects :? :raw
-- :doc retrieve a user given the id.
SELECT * FROM projects;

-- :name create-project<! :<!
-- :doc creates a new project, returning its id
INSERT INTO projects
(title, description)
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
