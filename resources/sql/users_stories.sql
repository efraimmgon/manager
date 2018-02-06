-- :name assign-user-to-story<! :<!
INSERT INTO users_stories
(user_id, story_id)
VALUES (:user-id, :story-id)
RETURNING users_stories_id;

-- :name deassign-user-from-story! :! :n
DELETE FROM users_stories
WHERE user_id = :user-id
  AND story_id = :story-id;
