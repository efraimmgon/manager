(ns manager.routes.services.tasks
  (:require
   [manager.db.core :as db]
   [ring.util.http-response :refer :all]
   [schema.core :as s]
   [schema.coerce :as coerce])
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
   (s/optional-key :status-name) s/Str
   (s/optional-key :priority-name) s/Str
   :created-at org.joda.time.DateTime
   :updated-at org.joda.time.DateTime})

(def TaskRequest (dissoc Task :task-id :created-at :updated-at :velocity))

(def parse-task-request
  (coerce/coercer TaskRequest coerce/json-coercion-matcher))

; (parse-task-request task-request)

(defn calculate-velocity
  "If the status == done, we calculate velocity like => orig-est / curr-est.
   Otherwise it's nil."
  [{:keys [status-id curr-est orig-est] :as params}]
  (assoc params :velocity
         (if (= "done" (:name (db/get-status {:status-id status-id})))
           (float (/ orig-est curr-est))
           nil)))

(defn delete-task! [params]
  (ok (db/delete-task! params)))

(defn get-task [params]
  (ok (db/get-task params)))

(defn get-tasks [params]
  (ok (db/get-tasks params)))

(defn create-task! [params]
  (ok
   (db/create-task<!
    (calculate-velocity params))))

(defn update-task! [params]
  (ok
   (db/update-task!
    (calculate-velocity params))))
