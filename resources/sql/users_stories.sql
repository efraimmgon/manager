-- :name assign-user-to-story<! :<!
INSERT INTO users_stories (user_id, story_id)
SELECT * FROM (SELECT :user-id, :story-id) AS tmp
WHERE NOT EXISTS (
  SELECT * FROM users_stories
   WHERE user_id = :user-id
     AND story_id = :story-id
) LIMIT 1
RETURNING users_stories_id;

-- :name deassign-user-from-story! :! :n
DELETE FROM users_stories
WHERE user_id = :user-id
  AND story_id = :story-id;
