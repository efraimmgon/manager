(ns manager.routes.services.users
  (:require
   [manager.db.core :as db]
   [ring.util.http-response :refer [ok internal-server-error]]))

(defn create-user<! [params]
  (ok (first (db/create-user<! params))))

(defn delete-user! [user-id-map]
  (ok (db/delete-user! user-id-map)))

(defn get-user [user-id-map]
  (ok (db/get-user user-id-map)))

(defn get-users []
  (ok (db/get-users)))

(defn update-user! [params]
  (ok (db/update-user! params)))
