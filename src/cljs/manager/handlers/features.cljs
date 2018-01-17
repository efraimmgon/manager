(ns manager.handlers.features
  (:require
   [ajax.core :as ajax]
   [manager.db :as db]
   [manager.handlers.tasks :refer [create-task! update-task!]]
   [manager.routes :refer [navigate!]]
   [manager.utils :refer [temp-id? done?]]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub]]
   [stand-lib.local-store :as ls]
   [stand-lib.re-frame.utils :refer [query]]))

(def feature-model
  {:feature-id :int
   :project-id :int
   :priority-id :int
   :status :keyword
   :title :str
   :description :str
   :created-at :timestamp
   :update-at :timestamp})

(defn feature-defaults [feature]
  (-> feature
      (update :description #(or % ""))
      (update :created-at #(or % (js/Date.)))
      (assoc :updated-at (js/Date.))))


; ------------------------------------------------------------------------------
; Subs
; ------------------------------------------------------------------------------

(reg-sub
 :features/done
 :<- [:features/all]
 (fn [features]
   (filter done? features)))

(reg-sub :features/feature query)

(reg-sub :features/all query)

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

; TODO
; A features estimate is the sum of all it's *pending* tasks current estimate.
(reg-sub
 :features/feature-estimate
 :<- [:features.feature/tasks]
 (fn [tasks]
   (->> tasks
        (filter (comp #{:done} :status))
        (map :curr-est)
        (reduce +))))

(reg-sub
 :features/pending
 :<- [:features/all]
 (fn [features]
   (filter (comp not done?) features)))

(reg-sub :features/show-completed? query)

(reg-sub
 :features.feature/tasks
 :<- [:features/feature]
 (fn [feature]
   (vals (:tasks feature))))

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
          {:into (:ls-features db)
           :id :feature-id
           ;; update feature with default fields
           ;; assign the project-id to the feature
           :keyvals (dissoc (feature-defaults (assoc feature :project-id project-id)) :tasks)})]
     (doseq [task (vals (:tasks feature))]
       (create-task!
        (:ls-tasks db) (:feature-id newfeature) task))
     {:dispatch [:navigate (str "/projects/" project-id)]})))

;;; Delete features and their tasks
(reg-event-fx
 :features/delete-feature
 (fn [{:keys [db]} [_ project-id feature-id]]
   (ls/delete!
    {:from (:ls-features db)
     :where #(= (:feature-id %) feature-id)})
   (ls/delete!
    {:from (:ls-tasks db)
     :where #(= (:feature-id %) feature-id)})
   {:dispatch [:navigate (str "/projects/" project-id)]}))

;;; Update feature and already existent tasks
;;; Create new tasks
(reg-event-fx
 :features/update-feature
 (fn [{:keys [db]} [_ feature]]
   (ls/update! (:ls-features db)
               {:set (dissoc (feature-defaults feature)
                             :tasks)
                :where #(= (:feature-id %) (:feature-id feature))})
   (let [old-tasks (filter (comp not temp-id? :task-id) (vals (:tasks feature)))
         new-tasks (filter (comp temp-id? :task-id) (vals (:tasks feature)))]
     (doseq [task new-tasks]
       (create-task! (:ls-tasks db) (:feature-id feature) task))
     (doseq [task old-tasks]
       (update-task! (:ls-tasks db) task)))
   {:dispatch [:navigate (str "/projects/" (:project-id feature))]}))

(reg-event-fx
 :features/load-feature
 (fn [{:keys [db]} [_ feature-id]]
   {:dispatch [:features/set-feature
               (first
                 (ls/select {:from (:ls-features db)
                             :where #(= (:feature-id %) feature-id)}))]}))
(reg-event-fx
 :features/load-features-for
 (fn [{:keys [db]} [_ project-id]]
   (let [feats (ls/select {:from (:ls-features db)
                           :where #(= (:project-id %) project-id)
                           :order-by [:priority <]})]
     {:dispatch [:features/set-features feats]})))

(reg-event-db
 :features/set-features
 (fn [db [_ features]]
   (assoc-in db [:features :all] features)))

(reg-event-db
 :features/set-feature
 (fn [db [_ feature]]
   (assoc-in db [:features :feature] feature)))

(reg-event-db
 :features/toggle-show-completed
 (fn [db _]
   (update-in db [:features :show-completed?] not)))
