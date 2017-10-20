(ns manager.routes.services.projects
  (:require
   [manager.db.core :as db]
   [schema.core :as s]
   [ring.util.http-response :refer :all])
  (:import
   [org.joda.time]))

(def Project
  {:project-id s/Int
   :title s/Str
   :description s/Str
   :created-at org.joda.time.DateTime
   :updated-at org.joda.time.DateTime})

(defn delete-project! [params]
  (ok (db/delete-project! params)))


(defn get-all-projects []
  (ok (db/get-all-projects)))

(defn create-project!
  "Create a project, returning the project-id"
  [params]
  (ok (db/create-project<! params)))

(defn get-project [{:keys [project-id] :as params}]
  (ok (db/get-project params)))

(defn update-project! [params]
  (ok (db/update-project! params)))
