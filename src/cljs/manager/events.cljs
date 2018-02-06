(ns manager.events
  (:require
   [ajax.core :as ajax]
   [manager.db :refer [default-db]]
   [manager.routes :refer [navigate!]]
   [manager.handlers.tasks]
   [manager.handlers.stories]
   [manager.handlers.projects]
   [manager.handlers.users]
   [manager.utils :refer [interceptors]]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub subscribe]]
   [stand-lib.handlers]
   [stand-lib.re-frame.utils :refer [query]]))

; ------------------------------------------------------------------------------
; Events
; ------------------------------------------------------------------------------

; Helpers ----------------------------------------------------------------------

(reg-event-fx
 :ajax-error
 interceptors
 (fn [_ [response]]
   (js/console.log response)
   (dispatch [:set-error (-> response :response :errors)])))

(reg-event-db
  :initialize-db
  interceptors
  (fn [_ _]
    ; (dispatch [:load-priorities])
    ; (dispatch [:load-status])
    default-db))

(reg-event-fx
 :load-priorities
 interceptors
 (fn [_ _]
   (ajax/GET "/api/priorities"
             {:handler #(dispatch [:set-priorities %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-fx
 :load-status
 interceptors
 (fn [_ _]
   (ajax/GET "/api/status"
             {:handler #(dispatch [:set-status %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-fx
 :navigate
 interceptors
 (fn [db [url]]
   (navigate! url)
   nil))

(reg-event-db
  :set-active-page
  interceptors
  (fn [db [page]]
    (assoc db :page page)))

(reg-event-db
  :set-active-project
  interceptors
  (fn [db [id]]
    (assoc db :project id)))

(reg-event-db
  :set-docs
  interceptors
  (fn [db [docs]]
    (assoc db :docs docs)))

(reg-event-db
 :set-error
 interceptors
 (fn [db [error]]
   (assoc db :error error)))

(reg-event-db
 :update-history
 interceptors
 (fn [db [pathname]]
   (update db :history conj pathname)))

(reg-event-db
 :set-priorities
 interceptors
 (fn [db [priorities]]
   (assoc db :priorities priorities)))

(reg-event-db
 :set-status
 interceptors
 (fn [db [status]]
   (assoc db :status status)))

(reg-event-fx
 :set-title
 (fn [_ [_ new-title]]
   (set! (.-title js/document) new-title)))

; ------------------------------------------------------------------------------
; Subs
; ------------------------------------------------------------------------------

(reg-sub :page query)

(reg-sub :docs query)

(reg-sub
 :previous-page
 (fn [db _]
   (second (:history db))))

(reg-sub
 :db
 (fn [db _]
   db))

(reg-sub :ls-stories query)

(reg-sub :ls-tasks query)

(reg-sub :priorities query)

(reg-sub :status query)

(reg-sub :types query)

(reg-sub :error query)

(reg-sub
 :uncompleted-tasks
 :<- [:tasks]
 (fn [tasks _]
   (filter #(not= 2 (:status %)) tasks)))
