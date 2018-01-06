(ns manager.handlers.features
  (:require
   [ajax.core :as ajax]
   [manager.db :as db]
   [manager.routes :refer [navigate!]]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub]]
   [stand-lib.re-frame.utils :refer [query]]))


(defn feature-defaults [feature]
  (-> feature
      (update :description #(or % ""))))

; ------------------------------------------------------------------------------
; Subs
; ------------------------------------------------------------------------------

(reg-sub :feature query)

(reg-sub :features query)

(reg-sub :feature/tasks query)


; ------------------------------------------------------------------------------
; Events
; ------------------------------------------------------------------------------

(reg-event-db
 :close-feature
 (fn [db _]
   (dissoc db :feature)))

(reg-event-fx
 :create-feature
 (fn [{:keys [db]} [_ project-id feature]]
   (ajax/POST (str "/api/projects/" project-id "/features")
              {:params (feature-defaults feature)
               :handler #(navigate! (str "/projects/" project-id
                                         "/features/" (:feature-id (first %))))
               :error-handler #(dispatch [:ajax-error %])})
   nil))

(reg-event-fx
 :delete-feature
 (fn [{:keys [db]} [_ feature-id]]
   (ajax/DELETE "/api/features"
                {:params {:feature-id feature-id}
                 :handler #(navigate! (str "/projects/" (get-in db [:project :project-id])))
                 :error-handler #(dispatch [:ajax-error %])})
   nil))

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
 :load-feature
 (fn [{:keys [db]} [_ feature-id]]
   (ajax/GET (str "/api/features/" feature-id)
             {:handler #(dispatch [:set-feature %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-fx
 :load-features-for
 (fn [{:keys [db]} [_ project-id]]
   (ajax/GET (str "/api/projects/" project-id "/features")
             {:handler #(dispatch [:set-features %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-db
 :set-active-feature
 (fn [db [_ feature-id]]
   (assoc db :feature feature-id)))

(reg-event-db
 :set-features
 (fn [db [_ features]]
   (assoc db :features features)))

(reg-event-db
 :set-feature
 (fn [db [_ feature]]
   (assoc db :feature feature)))
