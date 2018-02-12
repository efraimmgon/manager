-- :name get-stories-by-project :? :raw
-- :doc get all stories by project-id
SELECT st.* FROM stories st
WHERE st.project_id = :project-id
ORDER BY st.priority_idx ASC;

-- :name get-story :? :1
-- :doc get story by :story-id
SELECT st.*, users_stories.user_id AS owner
FROM stories st
LEFT JOIN users_stories ON st.story_id = users_stories.story_id
WHERE st.story_id = :story-id;

-- :name create-story<! :<!
-- :doc create a story for :project-id, returning the :story-id
INSERT INTO stories
(title, description, project_id, priority_idx, status, type, deadline)
VALUES
(:title, :description, :project-id, :priority-idx, :status, :type, :deadline)
RETURNING story_id;

-- :name update-story! :! :n
-- :doc update story by :story-id
UPDATE stories
SET title = :title,
    description = :description,
    priority_idx = :priority-idx,
    status = :status,
    type = :type,
    deadline = :deadline,
    updated_at = (now() AT TIME ZONE 'utc')
WHERE story_id = :story-id;

-- :name delete-story! :! :n
-- :doc delete story by :story-id
DELETE FROM stories
WHERE story_id = :story-id;
