(ns manager.events
  (:require
   [ajax.core :as ajax]
   [manager.db :as db]
   [manager.routes :refer [navigate!]]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub]]))

(defn query [db [event-id]]
  (event-id db))

; ------------------------------------------------------------------------------
; Events
; ------------------------------------------------------------------------------

; Helpers ----------------------------------------------------------------------

(defn gen-features [n]
  (for [i (range 1 (inc n))]
    {:feature-id i
     :title (str "feature " i)
     :description "Nostrud occaecat ad ut veniam incididunt laborum elit anim."}))

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

(defn gen-projects [n]
  (for [i (range 1 (inc n))]
    {:project-id i
     :title (str "project " i)
     :description (str "project-desc " i)}))

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
   ; GET tasks for feature-id
   {:db (assoc db :tasks (sort-by :priority (gen-tasks 10)))}))

(reg-event-db
 :set-active-task
 (fn [db [_ project-id task-id]]
   (assoc db :task [project-id task-id])))

; Project ----------------------------------------------------------------------

(reg-event-db
 :close-project
 (fn [db _]
   (dissoc db :project)))

(reg-event-fx
 :create-project
 (fn [{:keys [db]} [_ project]]
   ; POST project params from DB
   ; after the key is returned from the DB:
   (navigate! (str "/projects/" 1))))

(reg-event-fx
 :delete-project
 (fn [{:keys [db]} [_ project-id]]
   ; DELETE project key from DB, then:
   (navigate! "/")))

(reg-event-fx
 :edit-project
 (fn [{:keys [db]} [_ project]]
   ; PUT feature params to server, then:
   (navigate! (str "/projects/" (:project-id project)))))

(reg-event-fx
 :load-project
 (fn [{:keys [db]} [_ project-id]]
   ; GET project by id
   {:db (assoc db :project
               {:project-id project-id :title (str "project " project-id)})}))

(reg-event-fx
 :load-projects
 (fn [{:keys [db]} _]
   ; GET all projects
   {:db (assoc db :projects (gen-projects 2))}))

; Features ---------------------------------------------------------------------

(reg-event-db
 :close-feature
 (fn [db _]
   (dissoc db :feature)))

(reg-event-fx
 :create-feature
 (fn [{:keys [db]} [_ feature]]
   ; POST feature params from DB
   ; after the key is returned from the DB:
   (navigate! (str "/projects/" (:project-id feature)
                   "/features/" 1))))

(reg-event-fx
 :delete-feature
 (fn [{:keys [db]} [_ feature-id]]
   ; DELETE feature key from DB, then:
   (navigate! (str "/projects/" (get-in db [:project :project-id])))))

(reg-event-fx
 :edit-feature
 (fn [{:keys [db]} [_ feature]]
   ; PUT feature params to server, then:
   (navigate! (str "/projects/" (get-in db [:project :project-id])))))

(reg-event-fx
 :load-feature
 (fn [{:keys [db]} [_ feature-id]]
   ; GET feature by id
   {:db (assoc db :feature (first (gen-features 1)))}))

(reg-event-fx
 :load-features-for
 (fn [{:keys [db]} [_ project-id]]
   ; GET features for project-id
   {:db (assoc db :features (gen-features 5))}))

(reg-event-db
 :set-active-feature
 (fn [db [_ feature-id]]
   (assoc db :feature feature-id)))

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

(reg-sub :projects query)

(reg-sub :project query)

; Features ---------------------------------------------------------------------

(reg-sub :feature query)

(reg-sub :features query)


; Tasks ------------------------------------------------------------------------

(reg-sub :task query)

(reg-sub :tasks query)

; Misc -------------------------------------------------------------------------

(reg-sub
  :page
  query)

(reg-sub
  :docs
  query)

(reg-sub
 :db
 (fn [db _]
   db))
