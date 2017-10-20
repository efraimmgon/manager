(ns manager.handlers.tasks
  (:require
   [ajax.core :as ajax]
   [manager.db :as db]
   [manager.routes :refer [navigate!]]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub]]))

(defn query [db [event-id]]
  (event-id db))

(defn gen-tasks [n]
  (for [i (range 1 (inc n))]
    {:task-id i
     :title (str "task " i)
     :description (str "task-description " i)
     :orig-est (rand-int 17)
     :curr-est (rand-int 17)
     :priority (inc (rand-int 8))
     :elapsed 0
     :remain 0
     :status "-"}))

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
 (fn [{:keys [db]} [_ task]]
   ; POST task params from DB
   ; after the key is returned from the DB:
   (navigate! (str "/projects/" (:project-id task)
                   "/features/" (:feature-id task)))))

(reg-event-fx
 :delete-task
 (fn [{:keys [db]} [_ task-id]]
   ; DELETE task key from DB, then:
   (navigate! (str "/projects/" (get-in db [:project :project-id])
                   "/features/" (get-in db [:feature :feature-id])))))

(reg-event-fx
 :edit-task
 (fn [{:keys [db]} [_ task]]
   ; PUT task params to server, then:
   (navigate! (str "/projects/" (get-in db [:project :project-id])
                   "/features/" (get-in db [:feature :feature-id])))))

(reg-event-fx
 :load-task
 (fn [{:keys [db]} [_ task-id]]
   ; GET task by id
   {:db (assoc db :task (first (gen-tasks 1)))}))

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
 :set-tasks
 (fn [db [_ tasks]]
   (assoc db :tasks tasks)))
