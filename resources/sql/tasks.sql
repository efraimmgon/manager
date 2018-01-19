-- :name get-task :? :1
-- :doc get task by task-id
SELECT t.*
FROM tasks t
WHERE t.task_id = :task-id;

-- :name get-tasks :? :raw
-- :doc get all tasks by story-id
SELECT t.* FROM tasks t
WHERE story_id = :story-id
ORDER BY t.status DESC, t.priority_idx_idx;

-- :name get-unfineshed-tasks-by-project :? :raw
-- :doc get tasks by project-id
SELECT t.* FROM tasks t
JOIN stories st ON t.story_id = st.story_id
WHERE st.project_id = :project-id
  AND stauts_id != 'done'
ORDER BY t.priority_idx;

-- :name get-recently-updated-tasks-by-project :? :raw
-- :doc get tasks by project-id ordered by update date desc
SELECT t.* FROM tasks t
JOIN stories st ON t.story_id = st.story_id
WHERE st.project_id = :project-id
ORDER BY t.updated_at DESC;

-- :name create-task<! :<!
-- :doc create a task for story-id, returning the :task-id
INSERT INTO tasks
(title, description, orig_est, curr_est, elapsed, remain, velocity,
 story_id, priority_idx, status)
VALUES (:title, :description, :orig-est, :curr-est, :elapsed, :remain,
        :velocity, :story-id, :priority-idx, :status)
RETURNING task_id;

-- :name update-task! :! :n
-- :doc update task by task-id
UPDATE tasks
SET story_id = :story-id,
    title = :title,
    description = :description,
    orig_est = :orig-est,
    curr_est = :curr-est,
    elapsed = :elapsed,
    remain = :remain,
    priority_idx = :priority-idx,
    status = :status,
    velocity = :velocity,
    updated_at = (now() AT TIME ZONE 'utc')
WHERE task_id = :task-id;

-- :name delete-task! :! :n
-- :doc delete task by task-id
DELETE FROM tasks
WHERE task_id = :task-id;
