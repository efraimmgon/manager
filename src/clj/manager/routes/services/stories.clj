(ns manager.routes.services.stories
  (:require
   [manager.db.core :as db]
   [ring.util.http-response :refer :all]
   [schema.core :as s])
  (:import
   [org.joda.time]))

(def story
  {:story-id s/Int
   :project-id s/Int
   :title s/Str
   :description s/Str
   :created-at org.joda.time.DateTime
   :updated-at org.joda.time.DateTime
   (s/optional-key :pending-task-count) (s/maybe s/Int)})

(defn get-stories-by-project [{:keys [project-id] :as params}]
  (ok (db/get-stories-by-project params)))

(defn create-story! [params]
  (ok (db/create-story<! params)))

(defn delete-story! [params]
  (ok (db/delete-story! params)))

(defn get-story [params]
  (ok (db/get-story params)))

(defn update-story! [params]
  (ok (db/update-story! params)))
