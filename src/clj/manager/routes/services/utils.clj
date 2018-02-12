(ns manager.routes.services.utils
  (:require
   [clj-time.core :as t]
   [clj-time.format :as tf]))

(defn parse-date [format s]
  (let [formats {:date-time :date-time}]
    (tf/parse (tf/formatters (get formats format))
              s)))

(defn with-velocity
  "If the status == done, we calculate velocity like => orig-est / curr-est.
   Otherwise it's nil."
  [{:keys [status curr-est orig-est] :as params}]
  (assoc params :velocity
         (if (= "done" (:status params))
           (float (/ orig-est curr-est))
           nil)))
