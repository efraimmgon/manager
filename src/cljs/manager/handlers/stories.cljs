(ns manager.handlers.stories
  (:require
   [ajax.core :as ajax]
   [manager.db :as db]
   [manager.handlers.tasks :refer [create-task! update-task!]]
   [manager.routes :refer [navigate!]]
   [manager.utils :refer [temp-id? done? interceptors]]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub]]
   [stand-lib.local-store :as ls]
   [stand-lib.re-frame.utils :refer [query]]))

(def story-model
  {:story-id :int
   :project-id :int
   :priority-idx :int
   :status :int
   :title :str
   :type :int
   :description :str
   :created-at :timestamp
   :update-at :timestamp})

(defn story-defaults [story]
  (-> story
      (update :description #(or % ""))
      (update :created-at #(or % (js/Date.)))
      (assoc :updated-at (js/Date.))))


; ------------------------------------------------------------------------------
; Subs
; ------------------------------------------------------------------------------

(reg-sub
 :stories/done
 :<- [:stories/all]
 (fn [stories]
   (filter done? stories)))

(reg-sub :stories/story query)

(reg-sub :stories/all query)

(reg-sub
 :stories/story-tasks-counter
 (fn [db]
   (or (get-in db [:stories :story-tasks-counter])
       0)))

(reg-sub
 :stories/story-tasks-indexes
 (fn [db]
   (or (get-in db [:stories :story-tasks-indexes])
       #{})))

; TODO
; A stories estimate is the sum of all it's *pending* tasks current estimate.
(reg-sub
 :stories/story-estimate
 :<- [:stories.story/tasks]
 (fn [tasks]
   (->> tasks
        (filter (comp #{"done"} :status))
        (map :curr-est)
        (reduce +))))

(reg-sub
 :stories/pending
 :<- [:stories/all]
 (fn [stories]
   (filter (comp not done?) stories)))

(reg-sub :stories/show-completed? query)

(reg-sub
 :stories.story/tasks
 :<- [:stories/story]
 (fn [story]
   (vals (:tasks story))))

; ------------------------------------------------------------------------------
; Events
; ------------------------------------------------------------------------------

(reg-event-db
 :stories/story-tasks-tick
 interceptors
 (fn [db _]
   (let [temp-id (keyword (gensym "task-id"))]
     (update-in db [:stories :story :tasks]
                assoc temp-id {:task-id temp-id}))))
     ; (-> (update-in db [:stories :story-tasks-counter] inc)
     ;     (update-in [:stories :story-tasks-indexes] (fnil conj #{}) idx))))

(reg-event-db
 :stories/cancel-task
 interceptors
 (fn [db [idx]]
   (-> (update-in db [:stories :story :tasks] dissoc idx)
       (update-in [:stories :story-tasks-indexes] disj idx))))


(reg-event-db
 :stories/close-story
 interceptors
 (fn [db _]
   (update db :stories dissoc
           :story :story-tasks-counter :story-tasks-indexes)))

(reg-event-fx
 :stories/create-story-with-tasks
 interceptors
 (fn [{:keys [db]} [project-id story]]
   (ajax/PUT (str "/api/projects/" project-id "stories")
             {:params story
              :handler #(prn %)
              :error-handler #(dispatch [:ajax-error %])})
   nil))
    ; {:dispatch [:navigate (str "/projects/" project-id)]})))

;;; Delete stories and their tasks
(reg-event-fx
 :stories/delete-story
 interceptors
 (fn [{:keys [db]} [project-id story-id]]
   (ls/delete!
    {:from (:ls-stories db)
     :where #(= (:story-id %) story-id)})
   (ls/delete!
    {:from (:ls-tasks db)
     :where #(= (:story-id %) story-id)})
   {:dispatch [:navigate (str "/projects/" project-id)]}))

;;; Update story and already existent tasks
;;; Create new tasks
(reg-event-fx
 :stories/update-story
 interceptors
 (fn [{:keys [db]} [story]]
   (ls/update! (:ls-stories db)
               {:set (dissoc (story-defaults story)
                             :tasks)
                :where #(= (:story-id %) (:story-id story))})
   (let [old-tasks (filter (comp not temp-id? :task-id) (vals (:tasks story)))
         new-tasks (filter (comp temp-id? :task-id) (vals (:tasks story)))]
     (doseq [task new-tasks]
       (create-task! (:ls-tasks db) (:story-id story) task))
     (doseq [task old-tasks]
       (update-task! (:ls-tasks db) task)))
   {:dispatch [:navigate (str "/projects/" (:project-id story))]}))

(reg-event-fx
 :stories/load-story
 interceptors
 (fn [{:keys [db]} [story-id]]
   {:dispatch [:stories/set-story
               (first
                 (ls/select {:from (:ls-stories db)
                             :where #(= (:story-id %) story-id)}))]}))
(reg-event-fx
 :stories/load-stories-for
 interceptors
 (fn [{:keys [db]} [project-id]]
   (ajax/GET (str "/api/projects/" project-id "/stories")
             {:handler #(dispatch [:stories/set-stories %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-db
 :stories/set-stories
 interceptors
 (fn [db [stories]]
   (assoc-in db [:stories :all] stories)))

(reg-event-db
 :stories/set-story
 interceptors
 (fn [db [story]]
   (assoc-in db [:stories :story] story)))

(reg-event-db
 :stories/toggle-show-completed
 interceptors
 (fn [db _]
   (update-in db [:stories :show-completed?] not)))
