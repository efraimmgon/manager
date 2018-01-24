(ns manager.handlers.projects
  (:require
   [ajax.core :as ajax]
   [manager.db :as db]
   [manager.routes :refer [navigate!]]
   [manager.utils :refer [interceptors]]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub]]
   [stand-lib.re-frame.utils :refer [query]]))

(defn project-defaults [project]
  (-> project
      (update :description #(or % ""))))
; ------------------------------------------------------------------------------
; Subs
; ------------------------------------------------------------------------------

(reg-sub :projects query)

(reg-sub :project query)

; ------------------------------------------------------------------------------
; Events
; ------------------------------------------------------------------------------

(reg-event-db
 :projects/close-project
 interceptors
 (fn [db _]
   (dissoc db :project)))

(reg-event-fx
 :create-project
 interceptors
 (fn [{:keys [db]} [project]]
   (ajax/POST "/api/projects"
              {:params (project-defaults project)
               :handler #(navigate! (str "/projects/" (:project-id (first %))))
               :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :delete-project
 interceptors
 (fn [{:keys [db]} [project-id]]
   (ajax/DELETE "/api/projects"
                {:params {:project-id project-id}
                 :handler #(navigate! "/")
                 :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :edit-project
 interceptors
 (fn [{:keys [db]} [project]]
   (ajax/PUT "/api/projects"
             {:params (select-keys project [:project-id :title :description])
              :handler #(navigate! (str "/projects/" (:project-id project)))
              :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :projects/load-project
 interceptors
 (fn [{:keys [db]} [project-id]]
   ; GET project by id
   (ajax/GET (str "/api/projects/" project-id)
             {:handler #(dispatch [:set-project %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-fx
 :projects/load-projects
 interceptors
 (fn [{:keys [db]} _]
   ; GET all projects
   (ajax/GET "/api/projects"
             {:handler #(dispatch [:set-projects %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-db
 :set-project
 interceptors
 (fn [db [project]]
   (assoc db :project project)))

(reg-event-db
 :set-projects
 interceptors
 (fn [db [projects]]
   (assoc db :projects projects)))
