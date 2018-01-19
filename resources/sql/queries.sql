-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(id, first_name, last_name, email, pass)
VALUES (:id, :first_name, :last_name, :email, :pass)

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id

-- -----------------------------------------------------------------------------
-- Status
-- -----------------------------------------------------------------------------

-- :name get-all-status :? :*
-- :doc get all available status
SELECT * FROM status;

-- :name get-status :? :1
-- :doc get status by status
SELECT * FROM status
WHERE status = :status;

-- -----------------------------------------------------------------------------
-- Priorities
-- -----------------------------------------------------------------------------

-- :name get-priorities :? :*
-- :doc get all available priorities
SELECT * FROM priorities;
