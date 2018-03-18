-- :name create-story-relationship! :! :n
INSERT INTO story_relationships
(story_id1, story_id2)
VALUES (:story_id1, :story_id2);

-- :name delete-story-relationship! :! :n
DELETE FROM story_relationships
WHERE story_relationship_id = :story_relationship_id;
