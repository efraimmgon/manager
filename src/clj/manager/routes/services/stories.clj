(ns manager.routes.services.stories
  (:require
   [clojure.java.jdbc :as jdbc]
   [manager.db.core :as db]
   [manager.routes.services.utils :refer [with-velocity]]
   [ring.util.http-response :refer [ok internal-server-error]]))

(defn get-stories-by-project [{:keys [project-id] :as params}]
  (ok (db/get-stories-by-project params)))

(defn create-story-with-tasks! [params]
  (try
    (jdbc/with-db-transaction [tx db/*db*]
      (let [story-id-map (first (db/create-story<! tx params))]
        (when-let [owner-id (:owner params)]
          (db/assign-user-to-story<! tx
           (assoc story-id-map :user-id owner-id)))
        (doseq [task (:tasks params)]
          (let [new-task
                (-> task
                    (merge story-id-map)
                    with-velocity)]
            (db/create-task<! tx new-task)))
        (ok story-id-map)))
    (catch Exception e
      (internal-server-error (.getMessage e)))))

(defn deassign-user-from-story! [params]
  (ok (db/deassign-user-from-story! params)))

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
    (jdbc/with-db-transaction [tx db/*db*]
      (db/update-story! tx params)
      (when-let [owner-id (:owner params)]
        (db/assign-user-to-story<! tx
         {:user-id owner-id, :story-id (:story-id params)}))
      (doseq [task (filter :task-id (:tasks params))]
        (db/update-task! tx
         (with-velocity task)))
      (doseq [task (remove :task-id (:tasks params))]
        (let [new-task
              (-> task
                  (assoc :story-id (:story-id params))
                  with-velocity)]
          (db/create-task<! tx new-task)))
      (ok 1))
    (catch Exception e
      (internal-server-error (.getMessage e)))))
