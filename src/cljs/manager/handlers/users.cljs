(ns manager.handlers.users
  (:require
   [ajax.core :as ajax]
   [manager.utils :refer [interceptors]]
   [re-frame.core :refer [dispatch reg-sub reg-event-db reg-event-fx]]
   [stand-lib.re-frame.utils :refer [query <sub]]))

(reg-sub :users/all query)

(reg-event-fx
 :users/load-users
 interceptors
 (fn [_ _]
   (ajax/GET "/api/users"
             {:handler #(dispatch [:users/set-users %])
              :error-handler #(dispatch [:ajax-error %])
              :keywords? true
              :response-format :json})
   nil))

(reg-event-db
 :users/set-users
 interceptors
 (fn [db [users]]
   (assoc-in db [:users :all] users)))
