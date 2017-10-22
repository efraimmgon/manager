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

-- -----------------------------------------------------------------------------
-- Features
-- -----------------------------------------------------------------------------

-- :name get-features-by-project :? :raw
-- :doc get all features by project-id
SELECT * FROM features f
WHERE f.project_id = :project-id;

-- :name get-feature :? :1
-- :doc get feature by :feature-id
SELECT * FROM features f
WHERE f.feature_id = :feature-id;

-- :name create-feature<! :<!
-- :doc create a feature for :project-id, returning the :feature-id
INSERT INTO features
(title, description, project_id)
VALUES (:title, :description, :project-id)
RETURNING feature_id;

-- :name update-feature! :! :n
-- :doc update feature by :feature-id
UPDATE features
SET title = :title,
    description = :description,
    updated_at = (now() AT TIME ZONE 'utc')
WHERE feature_id = :feature-id;

-- :name delete-feature! :! :n
-- :doc delete feature by :feature-id
DELETE FROM features
WHERE feature_id = :feature-id;

-- -----------------------------------------------------------------------------
-- Tasks
-- -----------------------------------------------------------------------------

-- :name get-task :? :1
-- :doc get task by task-id
SELECT * FROM tasks
WHERE task_id = :task-id;

-- :name get-tasks :? :raw
-- :doc get all tasks by feature-id
SELECT * FROM tasks
WHERE feature_id = :feature-id

-- :name create-task<! :<!
-- :doc create a task for feature-id, returning the :task-id
INSERT INTO tasks
(title, description, orig_est, curr_est, elapsed, remain, velocity,
 feature_id, priority_id, status_id)
VALUES (:title, :description, :orig-est, :curr-est, :elapsed, :remain,
        :velocity, :feature-id, :priority-id, :status-id)
RETURNING task_id;

-- :name update-task! :! :n
-- :doc update task by task-id
UPDATE tasks
SET feature_id = :feature-id,
    title = :title,
    description = :description,
    orig_est = :orig-est,
    curr_est = :curr-est,
    elapsed = :elapsed,
    remain = :remain,
    priority_id = :priority-id,
    status_id = :status-id,
    velocity = :velocity,
    updated_at = (now() AT TIME ZONE 'utc')
WHERE task_id = :task-id;

-- -----------------------------------------------------------------------------
-- Status
-- -----------------------------------------------------------------------------

-- :name get-all-status :? :*
-- :doc get all available status
SELECT * FROM status;

-- -----------------------------------------------------------------------------
-- Priorities
-- -----------------------------------------------------------------------------

-- :name get-priorities :? :*
-- :doc get all available priorities
SELECT * FROM priorities;
