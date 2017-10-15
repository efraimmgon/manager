(ns manager.handlers.projects
  (:require
   [ajax.core :as ajax]
   [manager.db :as db]
   [manager.routes :refer [navigate!]]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub]]))

(defn query [db [event-id]]
  (event-id db))

(defn gen-projects [n]
  (for [i (range 1 (inc n))]
    {:project-id i
     :title (str "project " i)
     :description (str "project-desc " i)}))

; ------------------------------------------------------------------------------
; Subs
; ------------------------------------------------------------------------------

(reg-sub :projects query)

(reg-sub :project query)

; ------------------------------------------------------------------------------
; Events
; ------------------------------------------------------------------------------

(reg-event-db
 :close-project
 (fn [db _]
   (dissoc db :project)))

(reg-event-fx
 :create-project
 (fn [{:keys [db]} [_ project]]
   (ajax/POST "/api/projects"
              {:params @project
               :handler #(do
                          (js/console.log "result => " %)
                          (navigate! (str "/projects/" (:project-id (first %)))))
               :error-handler #(js/console.log %)})))

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
   (ajax/GET "/api/projects"
             {:handler #(dispatch [:set-projects %])
              :error-handler #(js/console.log %)})
   nil))

(reg-event-db
 :set-projects
 (fn [db [_ projects]]
   (assoc db :projects projects)))
