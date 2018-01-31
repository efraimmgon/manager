-- :name get-stories-by-project :? :raw
-- :doc get all stories by project-id
SELECT st.* FROM stories st
WHERE st.project_id = :project-id
ORDER BY st.priority_idx ASC;

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
    priority_idx = :priority-idx,
    status = :status,
    type = :type,
    updated_at = (now() AT TIME ZONE 'utc')
WHERE story_id = :story-id;

-- :name delete-story! :! :n
-- :doc delete story by :story-id
DELETE FROM stories
WHERE story_id = :story-id;
