(ns manager.routes.services.projects)
(ns manager.routes.services.projects
  (:require
   [manager.db.core :as db]
   [clojure.spec.alpha :as s]
   [ring.util.http-response :refer :all])
  (:import
   [org.joda.time]))

(defn delete-project! [params]
  (let [ret (db/delete-project! params)]
    (ok ret)))


(defn get-all-projects [req]
  (ok (db/get-all-projects)))

(defn create-project!
  "Create a project, returning the project-id"
  [params]
  (ok (first (db/create-project<! params))))

(defn get-project [{:keys [project-id] :as params}]
  (ok (db/get-project params)))

(defn update-project! [params]
  (ok (db/update-project! params)))
