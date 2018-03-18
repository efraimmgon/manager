-- :name create-activity-log!
INSERT INTO activities_history
(user_id, verb, property, entity, entity_id, old_value, new_value)
VALUES
(:user_id, :verb, :property, :entity, :entity_id, :old_value, :new_value);

-- get-activities-log-by-user
SELECT * FROM activities_history
WHERE user_id = :user_id;

-- get-activities-log-by-entity
SELECT * FROM activities_history
WHERE entity = :entity
  AND entity_id = :entity_id;
