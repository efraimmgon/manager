(ns manager.events
  (:require
   [ajax.core :as ajax]
   [manager.db :as db]
   [manager.routes :refer [navigate!]]
   [manager.handlers.tasks]
   [manager.handlers.features]
   [manager.handlers.projects]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub]]))

(defn query [db [event-id]]
  (event-id db))

; ------------------------------------------------------------------------------
; Events
; ------------------------------------------------------------------------------

; Helpers ----------------------------------------------------------------------


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

(reg-event-fx
 :ajax-error
 (fn [_ [_ error]]
   (js/console.log error)
   nil))

; ------------------------------------------------------------------------------
; Subs
; ------------------------------------------------------------------------------

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
