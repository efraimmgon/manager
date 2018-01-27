(ns manager.routes.services.stories
  (:require
   [manager.db.core :as db]
   [manager.routes.services.utils :refer [with-velocity]]
   [ring.util.http-response :refer [ok]]))


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

(defn update-story! [params]
  (ok (db/update-story! params)))
