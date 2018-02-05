(ns manager.handlers.users
  (:require
   [ajax.core :as ajax]
   [manager.utils :refer [interceptors]]
   [re-frame.core :refer [dispatch reg-sub reg-event-db reg-event-fx]]
   [stand-lib.re-frame.utils :refer [query <sub]]))

(defn user-defaults [u]
  (-> u
      (update :first-name #(or % ""))
      (update :last-name #(or % ""))))

(reg-sub :users/all query)

(reg-sub :users/user query)

(reg-event-db
 :users/close-user
 interceptors
 (fn [db _]
   (update db :users dissoc :user)))

(reg-event-fx
 :users/create-user
 interceptors
 (fn [_ [user]]
   ; create user
   ; navigate to user
   (ajax/POST "/api/users"
              {:params (user-defaults user)
               :handler #(dispatch [:navigate "/users"])
               :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :users/load-users
 interceptors
 (fn [_ _]
   (ajax/GET "/api/users/"
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
