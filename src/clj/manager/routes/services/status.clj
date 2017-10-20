(ns manager.routes.services.status
  (:require
   [manager.db.core :as db]
   [ring.util.http-response :refer :all]
   [schema.core :as s]))

(def Status
  {:status-id s/Int
   :name s/Str})

(defn get-all-status []
  (ok (db/get-all-status)))
