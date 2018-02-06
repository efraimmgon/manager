(ns manager.routes.services.users
  (:require
   [manager.db.core :as db]
   [ring.util.http-response :refer [ok internal-server-error]]))

(defn get-users []
  (ok (db/get-users)))

(defn create-user<! [params]
  (ok (first (db/create-user<! params))))
