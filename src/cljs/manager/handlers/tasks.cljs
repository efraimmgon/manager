(ns manager.handlers.tasks
  (:require
   [ajax.core :as ajax]
   [manager.db :as db]
   [manager.routes :refer [navigate!]]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub]]))

(defn query [db [event-id]]
  (event-id db))

; ------------------------------------------------------------------------------
; Subs
; ------------------------------------------------------------------------------

(reg-sub :task query)

(reg-sub :tasks query)

; ------------------------------------------------------------------------------
; Events
; ------------------------------------------------------------------------------

(reg-event-db
 :close-task
 (fn [db _]
   (dissoc db :task)))

(reg-event-fx
 :create-task
 (fn [{:keys [db]} [_ project-id feature-id task]]
   (ajax/POST (str "/api/features/" feature-id "/tasks")
              {:params (assoc @task :feature-id feature-id)
               :handler #(navigate! (str "/projects/" project-id
                                         "/features/" feature-id))
               :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :delete-task
 (fn [{:keys [db]} [_ task-id]]
   (ajax/DELETE "/api/tasks"
                {:params {:task-id task-id}
                 :handler #(navigate! (str "/projects/" (get-in db [:project :project-id])
                                           "/features/" (get-in db [:feature :feature-id])))
                 :error-handler #(dispatch [:ajax-error %])})
   nil))


(reg-event-fx
 :edit-task
 (fn [{:keys [db]} [_ project-id task]]
   (ajax/PUT "/api/tasks"
             {:params (dissoc task :created-at :updated-at :velocity)
              :handler #(navigate! (str "/projects/" project-id
                                        "/features/" (:feature-id task)))
              :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :load-task
 (fn [{:keys [db]} [_ task-id]]
   (ajax/GET (str "/api/tasks/" task-id)
             {:handler #(dispatch [:set-task %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-fx
 :load-tasks-for
 (fn [{:keys [db]} [_ feature-id]]
   (ajax/GET (str "/api/features/" feature-id "/tasks")
             {:handler #(dispatch [:set-tasks %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-db
 :set-task
 (fn [db [_ task]]
   (assoc db :task task)))

(reg-event-db
 :set-tasks
 (fn [db [_ tasks]]
   (assoc db :tasks tasks)))
