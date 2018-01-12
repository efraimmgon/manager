(ns manager.handlers.features
  (:require
   [ajax.core :as ajax]
   [manager.db :as db]
   [manager.routes :refer [navigate!]]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub]]
   [stand-lib.local-store :as ls]
   [stand-lib.re-frame.utils :refer [query]]))

(def feature-model
  {:feature-id :int
   :project-id :int
   :priority-id :int
   ;; not sure how I should add an status field for feature. I'll leave it out
   ;; for now, since it will be easy to update with local-store
   ; :status-id :int
   :title :str
   :description :str
   :created-at :timestamp
   :update-at :timestamp})

(defn feature-defaults [feature project-id]
  (-> feature
      (assoc :project-id project-id)
      (update :description #(or % ""))
      (update :created-at #(or % (js/Date.)))
      (update :updated-at #(or % (js/Date.)))))

(defn task-defaults [task feature-id]
  (-> task
      (assoc :feature-id feature-id)
      (update :created-at #(or % (js/Date.)))
      (update :updated-at #(or % (js/Date.)))))

; ------------------------------------------------------------------------------
; Subs
; ------------------------------------------------------------------------------

(reg-sub :features/feature query)

(reg-sub :features/features query)

(reg-sub
 :features/feature-tasks-counter
 (fn [db]
   (or (get-in db [:features :feature-tasks-counter])
       0)))

(reg-sub
 :features/feature-tasks-indexes
 (fn [db]
   (or (get-in db [:features :feature-tasks-indexes])
       #{})))

(reg-sub
 :features.feature/tasks
 (fn [db]
   (vals (get-in db [:features :feature :tasks]))))

; ------------------------------------------------------------------------------
; Events
; ------------------------------------------------------------------------------

(reg-event-db
 :features/feature-tasks-tick
 (fn [db _]
   (let [temp-id (keyword (gensym "task-id"))]
     (update-in db [:features :feature :tasks]
                assoc temp-id {:task-id temp-id}))))
     ; (-> (update-in db [:features :feature-tasks-counter] inc)
     ;     (update-in [:features :feature-tasks-indexes] (fnil conj #{}) idx))))

(reg-event-db
 :features/cancel-task
 (fn [db [_ idx]]
   (-> (update-in db [:features :feature :tasks] dissoc idx)
       (update-in [:features :feature-tasks-indexes] disj idx))))


(reg-event-db
 :features/close-feature
 (fn [db _]
   (update db :features dissoc
           :feature :feature-tasks-counter :feature-tasks-indexes)))

(reg-event-fx
 :features/create-feature
 (fn [{:keys [db]} [_ project-id feature]]
   (let [tasks (:tasks feature)
         newfeature
         (ls/insert!
          {:into (:ls/features db)
           :id :feature-id
           ;; update feature with default fields
           ;; assign the project-id to the feature
           :keyvals (select-keys (feature-defaults feature project-id)
                                 [:title :description :priority-id :project-id])})]
     (doseq [task (vals (:tasks feature))]
       (ls/insert!
        {:into (:ls/tasks db)
         :id :task-id
         ;; update task with default fields
         ;; assign the feature-id to the task
         :keyvals (task-defaults task (:feature-id newfeature))}))
     {:dispatch [:navigate (str "/projects/" project-id)]})))

;;; Delete features and their tasks
(reg-event-fx
 :features/delete-feature
 (fn [{:keys [db]} [_ project-id feature-id]]
   (ls/delete!
    {:from (:ls/features db)
     :where #(= (:feature-id %) feature-id)})
   (ls/delete!
    {:from (:ls/tasks db)
     :where #(= (:feature-id %) feature-id)})
   {:dispatch [:navigate (str "/projects/" project-id)]}))

(reg-event-fx
 :edit-feature
 (fn [{:keys [db]} [_ feature]]
   (ajax/PUT "/api/features"
             {:params (select-keys feature [:feature-id :title :description])
              :handler #(navigate! (str "/projects/" (get-in db [:project :project-id])
                                        "/features/" (:feature-id feature)))
              :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :features/load-feature
 (fn [{:keys [db]} [_ feature-id]]
   {:dispatch [:features/set-feature
               (first
                 (ls/select {:from (:ls/features db)
                             :where #(= (:feature-id %) feature-id)}))]}))
(reg-event-fx
 :features/load-features-for
 (fn [{:keys [db]} [_ project-id]]
   (let [feats (ls/select {:from (:ls/features db)
                           :where #(= (:project-id %) project-id)})]
     {:dispatch [:features/set-features feats]})))

(reg-event-db
 :set-active-feature
 (fn [db [_ feature-id]]
   (assoc db :feature feature-id)))

(reg-event-db
 :features/set-features
 (fn [db [_ features]]
   (assoc-in db [:features :features] features)))

(reg-event-db
 :features/set-feature
 (fn [db [_ feature]]
   (assoc-in db [:features :feature] feature)))
