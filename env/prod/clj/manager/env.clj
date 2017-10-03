(ns manager.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[manager started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[manager has shut down successfully]=-"))
   :middleware identity})
