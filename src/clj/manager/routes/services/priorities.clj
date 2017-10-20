(ns manager.routes.services.priorities
  (:require
   [manager.db.core :as db]
   [ring.util.http-response :refer :all]
   [schema.core :as s]))

(def Priority
  {:priority-id s/Int
   :name s/Str})

(defn get-priorities []
  (ok (db/get-priorities)))
