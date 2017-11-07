(ns manager.routes.services.features
  (:require
   [manager.db.core :as db]
   [ring.util.http-response :refer :all]
   [schema.core :as s])
  (:import
   [org.joda.time]))

(def Feature
  {:feature-id s/Int
   :project-id s/Int
   :title s/Str
   :description s/Str
   :created-at org.joda.time.DateTime
   :updated-at org.joda.time.DateTime
   (s/optional-key :pending-task-count) (s/maybe s/Int)})

(defn get-features-by-project [{:keys [project-id] :as params}]
  (ok (db/get-features-by-project params)))

(defn create-feature! [params]
  (ok (db/create-feature<! params)))

(defn delete-feature! [params]
  (ok (db/delete-feature! params)))

(defn get-feature [params]
  (ok (db/get-feature params)))

(defn update-feature! [params]
  (ok (db/update-feature! params)))
