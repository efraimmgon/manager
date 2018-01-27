(ns manager.routes.services.utils)

(defn with-velocity
  "If the status == done, we calculate velocity like => orig-est / curr-est.
   Otherwise it's nil."
  [{:keys [status curr-est orig-est] :as params}]
  (assoc params :velocity
         (if (= "done" (:status params))
           (float (/ orig-est curr-est))
           nil)))
