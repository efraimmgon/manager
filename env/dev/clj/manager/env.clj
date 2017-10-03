(ns manager.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [manager.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[manager started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[manager has shut down successfully]=-"))
   :middleware wrap-dev})
