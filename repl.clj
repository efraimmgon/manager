(in-ns 'user)

;;; recompile queries
(in-ns 'manager.db.core)
(conman/bind-connection *db* "sql/queries.sql")
;;; recompile namespaces
(clojure.tools.namespace.repl/refresh)


;;; misc
(start)
(stop)
(restart)
