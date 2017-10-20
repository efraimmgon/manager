(ns manager.routes.services.tasks
  (:require
   [manager.db.core :as db]
   [ring.util.http-response :refer :all]
   [schema.core :as s])
  (:import
   [org.joda.time]))

(def Task
  {:task-id s/Int
   :title s/Str
   :description s/Str
   :orig-est s/Int
   :curr-est s/Int
   :elapsed s/Int
   :remain s/Int
   :velocity (s/maybe s/Num)
   :feature-id s/Int
   :priority-id s/Int
   :status-id s/Int
   :created-at org.joda.time.DateTime
   :updated-at org.joda.time.DateTime})

(defn get-tasks [params]
  (ok (db/get-tasks params)))

(defn create-task! [params]
  "todo")
