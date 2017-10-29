(ns manager.events
  (:require
   [ajax.core :as ajax]
   [manager.db :as db]
   [manager.routes :refer [navigate!]]
   [manager.handlers.tasks]
   [manager.handlers.features]
   [manager.handlers.projects]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub subscribe]]))

(defn query [db [event-id]]
  (event-id db))

(defn <sub [query-v]
  (deref (subscribe query-v)))

; ------------------------------------------------------------------------------
; Events
; ------------------------------------------------------------------------------

; Helpers ----------------------------------------------------------------------

(reg-event-fx
 :ajax-error
 (fn [_ [_ response]]
   (js/console.log response)
   (dispatch [:set-error (-> response :response :errors)])))

(reg-event-db
  :initialize-db
  (fn [_ _]
    (dispatch [:load-priorities])
    (dispatch [:load-status])
    db/default-db))

(reg-event-fx
 :load-priorities
 (fn [_ _]
   (ajax/GET "/api/priorities"
             {:handler #(dispatch [:set-priorities %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

(reg-event-fx
 :load-status
 (fn [_ _]
   (ajax/GET "/api/status"
             {:handler #(dispatch [:set-status %])
              :error-handler #(dispatch [:ajax-error %])
              :response-format :json
              :keywords? true})
   nil))

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

(reg-event-db
 :set-error
 (fn [db [_ error]]
   (assoc db :error error)))

(reg-event-db
 :update-history
 (fn [db [_ pathname]]
   (update db :history conj pathname)))

(reg-event-db
 :set-priorities
 (fn [db [_ priorities]]
   (assoc db :priorities priorities)))

(reg-event-db
 :set-status
 (fn [db [_ status]]
   (assoc db :status status)))

(reg-event-db
 :update-state
 (fn [db [_ ks val]]
   (assoc-in db ks val)))

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

(reg-sub :priorities query)

(reg-sub :status query)

(reg-sub :error query)
