(ns manager.routes.services.tasks
  (:require
   [manager.db.core :as db]
   [ring.util.http-response :refer :all]))

(defn get-tasks [params]
  (ok (db/get-tasks params)))
