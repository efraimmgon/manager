(ns manager.handlers.tasks
  (:require
   [ajax.core :as ajax]
   [manager.db :as db]
   [manager.routes :refer [navigate!]]
   [manager.utils :refer [temp-id?]]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub]]
   [stand-lib.local-store :as ls]
   [stand-lib.re-frame.utils :refer [query]]))

(def task-model
  {:task-id :int
   :story-id :int
   :status :int
   :title :str
   :orig-est :float
   :curr-est :float
   :velocity :float
   :created-at :timestamp
   :update-at :timestamp})

(defn task-defaults [task story-id]
  (-> task
      (update :story-id #(or % story-id))
      (update :created-at #(or % (js/Date.)))
      (assoc :updated-at (js/Date.))))

(defn create-task! [ls-key story-id task]
  (ls/insert!
   {:into ls-key
    :id :task-id
    ;; update task with default fields
    ;; assign the story-id to the task
    :keyvals (task-defaults task story-id)}))

(defn update-task! [ls-key task]
  (ls/update!
   ls-key
   {:set (task-defaults task nil)
    :where #(= (:task-id %) (:task-id task))}))
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
 (fn [{:keys [db]} [_ project-id story-id task]]
   (ajax/POST (str "/api/stories/" story-id "/tasks")
              {:params (assoc (task-defaults task story-id) :story-id story-id)
               :handler #(navigate! (str "/projects/" project-id
                                         "/stories/" story-id))
               :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :tasks/delete-task
 (fn [{:keys [db]} [_ task-id]]
   ;; check if it isn't a temp-id
   (when-not (temp-id? task-id)
     (ls/delete!
      {:from (:ls-tasks db)
       :where #(= (:task-id %) task-id)}))
   {:db (update-in db [:stories :story :tasks] dissoc task-id)}))

(reg-event-fx
 :edit-task
 (fn [{:keys [db]} [_ project-id task]]
   (ajax/PUT "/api/tasks"
             {:params task
              :handler #(navigate! (str "/projects/" project-id
                                        "/stories/" (:story-id task)))
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
 :tasks/load-tasks-for
 (fn [{:keys [db]} [_ story-id]]
   (let [;; we want the tasks as a map with the id as key
         tasks
         (reduce (fn [acc task]
                   (assoc acc (:task-id task) task))
                 {} (ls/select {:from (:ls-tasks db)
                                :where #(= (:story-id %) story-id)}))]
     {:dispatch [:tasks/set-tasks tasks]})))

(reg-event-fx
 :load-project-tasks
 (fn [_ [_ project-id]]
   (ajax/GET (str "/api/projects/" project-id "/tasks/unfineshed")
             {:handler #(dispatch [:set-tasks %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-fx
 :load-recently-updated-tasks-by-project
 (fn [_ [_ project-id]]
   (ajax/GET (str "/api/projects/" project-id "/tasks/recently-updated")
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
 :tasks/set-tasks
 (fn [db [_ tasks]]
   (assoc-in db [:stories :story :tasks] tasks)))
