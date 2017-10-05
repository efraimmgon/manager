(ns manager.events
  (:require
   [manager.db :as db]
   [manager.routes :refer [navigate!]]
   [re-frame.core :refer [dispatch reg-event-db reg-sub]]))

(defn query [db [event-id]]
  (event-id db))

; ------------------------------------------------------------------------------
; Events
; ------------------------------------------------------------------------------

; Helpers ----------------------------------------------------------------------

(defn delete
  "`dissoc`s a task from the project's tasks."
  [db project-id task-id]
  (update-in db
             [:projects project-id :tasks]
             dissoc task-id))

(defn save
  "`assoc`s a task id with its task in its respective project"
  [db project-id task]
  (update-in db
             [:projects project-id :tasks]
             assoc (:id task) task))

; Tasks ------------------------------------------------------------------------

(reg-event-db
 :close-task
 (fn [db _]
   (dissoc db :task)))


(reg-event-db
 :create-task
 (fn [db [_ project-id task]]
   (let [next-id (-> (get-in db [:projects project-id :tasks])
                     (keys)
                     (last)
                     (inc))]
     ; POST task params from DB
     ; after the key is returned from the DB:
     (navigate! (str "/projects/" project-id))
     (save db project-id (assoc task :id next-id)))))



(reg-event-db
 :delete-task
 (fn [db [_ project-id task-id]]
   ; DELETE task key from DB, then:
   (delete db project-id task-id)))

(reg-event-db
 :edit-task
 (fn [db [_ project-id task]]
   ; PUT task params to server, then:
   (navigate! (str "/projects/" project-id))
   (save db project-id task)))


(reg-event-db
 :set-active-task
 (fn [db [_ project-id task-id]]
   (assoc db :task [project-id task-id])))

; Misc -------------------------------------------------------------------------

(reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :page page)))

(reg-event-db
  :set-active-project
  (fn [db [_ id]]
    (assoc db :project id)))

(reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))


; ------------------------------------------------------------------------------
; Subs
; ------------------------------------------------------------------------------

; Projects ---------------------------------------------------------------------

(reg-sub
 :projects
 query)

(reg-sub
  :project
  (fn [db _]
    (let [id (:project db)]
      (get-in db [:projects id]))))

; Tasks ------------------------------------------------------------------------

(reg-sub
 :task
 (fn [db _]
   (when-let [[project-id task-id] (:task db)]
     (get-in db [:projects project-id :tasks task-id]))))

(reg-sub
 :tasks
 :<- [:project]
 (fn [project _]
   (vals (:tasks project))))

; Misc -------------------------------------------------------------------------

(reg-sub
  :page
  query)

(reg-sub
  :docs
  query)
