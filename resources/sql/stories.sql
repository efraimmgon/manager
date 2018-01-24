-- :name get-stories-by-project :? :raw
-- :doc get all stories by project-id
SELECT st.*, t.pending_task_count FROM stories st
LEFT OUTER JOIN (
  SELECT t.story_id, COUNT(t.*) AS pending_task_count FROM tasks t
  JOIN stories st ON t.story_id = st.story_id
  WHERE st.project_id = :project-id
    AND st.status != 'done'
  GROUP BY t.story_id
) AS t ON t.story_id = st.story_id
WHERE st.project_id = :project-id;

-- :name get-story :? :1
-- :doc get story by :story-id
SELECT * FROM stories st
WHERE st.story_id = :story-id;

-- :name create-story<! :<!
-- :doc create a story for :project-id, returning the :story-id
INSERT INTO stories
(title, description, project_id, priority_idx, status, type)
VALUES (:title, :description, :project-id, :priority-idx, :status, :type)
RETURNING story_id;

-- :name update-story! :! :n
-- :doc update story by :story-id
UPDATE stories
SET title = :title,
    description = :description,
    updated_at = (now() AT TIME ZONE 'utc')
WHERE story_id = :story-id;

-- :name delete-story! :! :n
-- :doc delete story by :story-id
DELETE FROM stories
WHERE story_id = :story-id;
