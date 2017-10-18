(require '[manager.db.core :as db])
(require '[manager.routes.services.projects :as projects])
(in-ns 'user)

;;; recompile queries
(in-ns 'manager.db.core)
(conman/bind-connection *db* "sql/queries.sql")
;;; recompile namespaces
(clojure.tools.namespace.repl/refresh)


;;; misc
(start)
(restart)

(projects/create-project! {:title "new3" :description "project"})
(-> (db/all-projects)
    first
    :created-at)

(db/update-project!
 {:project-id 1
  :title "new-title"
  :description "new-description"})
