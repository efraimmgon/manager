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
