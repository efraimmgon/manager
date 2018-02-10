(ns manager.handlers.stories
  (:require
   [ajax.core :as ajax]
   [manager.db :as db]
   [manager.handlers.tasks :refer [create-task! update-task!]]
   [manager.routes :refer [navigate!]]
   [manager.utils :refer [temp-id? done? interceptors]]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub subscribe]]
   [stand-lib.local-store :as ls]
   [stand-lib.re-frame.utils :refer [query]]))

(defn story-defaults [story]
  (-> story
      (update :description #(or % ""))))

(defn- rows->map [rows id]
  (reduce (fn [m row]
            (assoc m (get row id) row))
          {} rows))

(defn remove-temp-ids [tasks]
  (map #(if (temp-id? (:task-id %))
          (dissoc % :task-id))
       tasks))

(defn update-status [tasks]
  (map #(if (:status %) %
          (assoc % :status "pending"))
       tasks))

(defn tasks-map->rows [tasks]
  (->> (map #(if (temp-id? (:task-id %))
               (dissoc % :task-id)
               %)
            (vals tasks))
       update-status))

; ------------------------------------------------------------------------------
; Subs
; ------------------------------------------------------------------------------

(reg-sub :stories/add-owner? query)

(reg-sub
 :stories/done
 :<- [:stories/all]
 (fn [stories]
   (filter done? stories)))

(reg-sub :stories/story query)

(reg-sub :stories/new-story query)

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

(reg-sub :stories/story-path query)

(reg-sub :stories/show-completed? query)

(reg-sub
 :stories.story/tasks
 :<- [:stories/story]
 (fn [story]
   (vals (:tasks story))))

(reg-sub
 :stories.new-story/tasks
 :<- [:stories/new-story]
 (fn [story]
   (vals (:tasks story))))

; ------------------------------------------------------------------------------
; Events
; ------------------------------------------------------------------------------

(reg-event-db
 :stories/story-tasks-tick
 interceptors
 (fn [db [story-name]]
   (let [temp-id (keyword (gensym "task-id"))]
     (update-in db [:stories story-name :tasks]
                assoc temp-id {:task-id temp-id}))))

(reg-event-db
 :stories/close-story
 interceptors
 (fn [db _]
   (update db :stories dissoc
           :new-story :story :story-tasks-counter :story-tasks-indexes)))

(reg-event-fx
 :stories/create-story-with-tasks
 interceptors
 (fn [{:keys [db]} [story]]
   (let [story-updated (update story :tasks tasks-map->rows)]
     (ajax/POST (str "/api/projects/" (:project-id story) "/stories")
               {:params story-updated
                :handler #(dispatch [:navigate (str "/projects/" (:project-id story))])
                :error-handler #(dispatch [:ajax-error %])})
     nil)))

(reg-event-fx
 :stories/deassign-user
 interceptors
 (fn [_ [owner-id story-id]]
   (ajax/DELETE (str "/api/stories/" story-id "/owner/" owner-id)
                {:error-handler #(dispatch [:ajax-error %])})
   {:dispatch-n [[:stories/set-add-owner false]
                 [:set-state (conj @(subscribe [:stories/story-path]) :owner)
                  nil]]}))

;;; Delete stories and their tasks
(reg-event-fx
 :stories/delete-story
 interceptors
 (fn [_ [project-id story-id]]
   (ajax/DELETE "/api/stories"
                {:params {:story-id story-id}
                 :handler #(dispatch [:navigate (str "/projects/" project-id)])
                 :error-handler #(dispatch [:ajax-error %])})
   nil))

;;; Update story and already existent tasks
;;; Create new tasks
(reg-event-fx
 :stories/update-story-with-tasks
 interceptors
 (fn [_ [story]]
   (ajax/PUT (str "/api/stories/" (:story-id story) "/with-tasks")
             {:params (update story :tasks tasks-map->rows)
              :handler #(dispatch [:navigate (str "/projects/" (:project-id story))])
              :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :stories/load-story-with-tasks
 interceptors
 (fn [{:keys [db]} [story-id]]
   (ajax/GET (str "/api/stories/" story-id "/with-tasks")
             {:handler #(dispatch [:stories/set-story
                                   (update % :tasks rows->map :task-id)])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))
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
 :stories/set-add-owner
 interceptors
 (fn [db [new-value]]
   (assoc-in db [:stories :add-owner?] new-value)))

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
 :stories/set-story-path
 interceptors
 (fn [db [path]]
   (assoc-in db [:stories :story-path] path)))

(reg-event-db
 :stories/toggle-show-completed
 interceptors
 (fn [db _]
   (update-in db [:stories :show-completed?] not)))
