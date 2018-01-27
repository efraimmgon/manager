-- :name get-task :? :1
-- :doc get task by task-id
SELECT t.*
FROM tasks t
WHERE t.task_id = :task-id;

-- :name get-tasks :? :raw
-- :doc get all tasks by story-id
SELECT t.* FROM tasks t
WHERE story_id = :story-id
ORDER BY t.status DESC

-- :name get-unfineshed-tasks-by-project :? :raw
-- :doc get tasks by project-id
SELECT t.* FROM tasks t
JOIN stories st ON t.story_id = st.story_id
WHERE st.project_id = :project-id
  AND t.status != 'done'

-- :name get-recently-updated-tasks-by-project :? :raw
-- :doc get tasks by project-id ordered by update date desc
SELECT t.* FROM tasks t
JOIN stories st ON t.story_id = st.story_id
WHERE st.project_id = :project-id
ORDER BY t.updated_at DESC;

-- :name create-task<! :<!
-- :doc create a task for story-id, returning the :task-id
INSERT INTO tasks
(title, orig_est, curr_est, velocity, story_id, status)
VALUES
(:title, :orig-est, :curr-est, :velocity, :story-id, :status)
RETURNING task_id;

-- :name update-task! :! :n
-- :doc update task by task-id
UPDATE tasks
SET story_id = :story-id,
    title = :title,
    orig_est = :orig-est,
    curr_est = :curr-est,
    status = :status,
    velocity = :velocity,
    updated_at = (now() AT TIME ZONE 'utc')
WHERE task_id = :task-id;

-- :name delete-task! :! :n
-- :doc delete task by task-id
DELETE FROM tasks
WHERE task_id = :task-id;
