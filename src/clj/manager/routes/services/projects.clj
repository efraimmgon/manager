(ns manager.routes.services.projects
  (:require
   [manager.db.core :as db]
   [ring.util.http-response :refer :all]))

(defn all-projects []
  (ok (db/all-projects)))

(defn create-project! [params]
  (ok (db/create-project<! params)))
