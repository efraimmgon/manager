-- :name get-task :? :1
-- :doc get task by task-id
SELECT t.*,
       p.name AS priority_name,
       s.name AS status_name
FROM tasks t
JOIN priorities p ON t.priority_id = p.priority_id
JOIN status s ON t.status_id = s.status_id
WHERE t.task_id = :task-id;

-- :name get-tasks :? :raw
-- :doc get all tasks by feature-id
SELECT t.* FROM tasks t
WHERE feature_id = :feature-id
ORDER BY t.status_id DESC, t.priority_id;

-- :name get-unfineshed-tasks-by-project :? :raw
-- :doc get tasks by project-id
SELECT t.* FROM tasks t
JOIN status s ON t.status_id = s.status_id
JOIN features f ON t.feature_id = f.feature_id
WHERE f.project_id = :project-id
  AND s.name != 'done'
ORDER BY t.priority_id;

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

-- :name delete-task! :! :n
-- :doc delete task by task-id
DELETE FROM tasks
WHERE task_id = :task-id;
