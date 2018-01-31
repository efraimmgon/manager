(ns manager.routes.services.stories
  (:require
   [manager.db.core :as db]
   [manager.routes.services.utils :refer [with-velocity]]
   [ring.util.http-response :refer [ok internal-server-error]]))

(defn get-stories-by-project [{:keys [project-id] :as params}]
  (ok (db/get-stories-by-project params)))

(defn create-story-with-tasks! [params]
  (let [story-id (first (db/create-story<! params))]
    (doseq [task (:tasks params)]
      (-> task
          (merge story-id)
          with-velocity
          db/create-task<!))
    (ok story-id)))

(defn delete-story! [params]
  (ok (db/delete-story! params)))

(defn get-story [params]
  (ok (db/get-story params)))

(defn get-story-with-tasks [params]
  (let [story (db/get-story params)
        tasks (db/get-tasks params)]
    (ok (assoc story :tasks tasks))))

(defn update-story! [params]
  (ok (db/update-story! params)))

(defn update-story-with-tasks! [params]
  (try
    (db/update-story! params)
    (doseq [task (filter :task-id (:tasks params))]
      (db/update-task!
       (with-velocity task)))
    (doseq [task (remove :task-id (:tasks params))]
      (-> task
          (assoc :story-id (:story-id params))
          with-velocity
          db/create-task<!))
    (ok 1)
    (catch Exception e
      (internal-server-error (.getMessage e)))))
