(ns user
  (:require [luminus-migrations.core :as migrations]
            [manager.config :refer [env]]
            [mount.core :as mount]
            [manager.figwheel :refer [start-fw stop-fw cljs]]
            manager.core))

(defn start []
  (mount/start-without #'manager.core/repl-server))

(defn stop []
  (mount/stop-except #'manager.core/repl-server))

(defn restart []
  (stop)
  (start))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))


