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

(defn find-by [f coll obj]
  (some #(when (= (f %) obj)
           %)
        coll))

(defn index-of [coll obj f]
  (loop [remain coll
         i 0]
    (cond
      (empty? remain) nil
      (= (f (first remain)) obj) i
      :default (recur (rest remain) (inc i)))))

(defn delete
  "`dissoc`s a task from the project's tasks."
  [db project-id task-id]
  (update-in db
             [:projects project-id :tasks]
             #(filter (fn [m]
                        (not= (:id m) task-id))
                      %)))

(defn save
  "`conj`s a task in its respective project"
  [db project-id task]
  (update-in db
             [:projects project-id :tasks]
             conj task))

; Tasks ------------------------------------------------------------------------

(reg-event-db
 :close-task
 (fn [db _]
   (dissoc db :task)))

(defn gen-next-id [db project-id]
  (->> (get-in db [:projects project-id :tasks])
       (map :id)
       (sort >)
       (first)
       (inc)))

;; NOTE: next-id, prototype only
(reg-event-db
 :create-task
 (fn [db [_ project-id task]]
   (let [next-id (or (:next-id db)
                     (gen-next-id db project-id))]
     ; POST task params from DB
     ; after the key is returned from the DB:
     (navigate! (str "/projects/" project-id))
     (-> (save db project-id (assoc task :id next-id))
         ;; re-sort
         (update-in [:projects project-id :tasks] #(sort-by :priority %))
         (assoc :next-id (inc next-id))))))

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
     (find-by :id
              (get-in db [:projects project-id :tasks])
              task-id))))

(reg-sub
 :tasks
 :<- [:project]
 (fn [project _]
   (:tasks project)))

; Misc -------------------------------------------------------------------------

(reg-sub
  :page
  query)

(reg-sub
  :docs
  query)
