(in-ns 'user)

;;; recompile queries
(in-ns 'manager.db.core)
(conman/bind-connection *db* "sql/queries.sql")
;;; recompile namespaces
(clojure.tools.namespace.repl/refresh)


;;; misc
(start)
(restart)


;;; Stories can be assigned to a user

; create users_stories table [ok]
;  > user-id
;  > story-id
; CREATE, DELETE sql functions [ok]


;;; assign user to story

; typehead input
; find users as typing
; new: create if not null
; update: on change => create or delete
