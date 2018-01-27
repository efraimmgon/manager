(ns manager.routes.services.tasks
  (:require
   [manager.db.core :as db]
   [manager.routes.services.utils :refer [with-velocity]]
   [ring.util.http-response :refer [ok]]))

(defn delete-task!
  "delete task by task-id"
  [params]
  (ok (db/delete-task! params)))

(defn get-task
  "get tasks by task-id"
  [params]
  (ok (db/get-task params)))

(defn get-tasks
  "get tasks by story-id"
  [params]
  (let [res (db/get-tasks params)]
    (println res)
    (ok res)))

(defn get-recently-updated-tasks-by-project
  [params]
  (ok (db/get-recently-updated-tasks-by-project params)))

(defn get-unfineshed-tasks-by-project
  "get tasks by project-id"
  [params]
  (ok (db/get-unfineshed-tasks-by-project params)))

(defn create-task!
  "create task with the given params"
  [params]
  (ok
   (db/create-task<!
    (with-velocity params))))

(defn update-task!
  "update task with the given params. `updated-at` is updated at the db"
  [params]
  (ok
   (db/update-task!
    (with-velocity params))))
