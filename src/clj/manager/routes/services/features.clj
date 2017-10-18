(ns manager.routes.services.features
  (:require
   [manager.db.core :as db]
   [ring.util.http-response :refer :all]))

(defn get-features-by-project [{:keys [project-id] :as params}]
  (ok (db/get-features-by-project params)))
