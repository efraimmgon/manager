(ns manager.routes
  (:require
   [accountant.core :as accountant]
   [re-frame.core :as rf]
   [secretary.core :as secretary]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType])
  (:import goog.History))

(defn run-events [events]
  (doseq [event events]
    (rf/dispatch event)))

(defn context-url [url]
  (str js/context url))

(defn navigate! [url]
  (accountant/navigate! (context-url url)))

; ------------------------------------------------------------------------------
; Routes
; ------------------------------------------------------------------------------

;; TODO: get project from db
(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :home]))

;; TODO: get project tasks from db
(secretary/defroute "/projects/:id" [id]
  (run-events [[:set-active-project (js/parseInt id)]
               [:set-active-page :project]]))

(secretary/defroute "/projects/:id/tasks/new" [id]
  (rf/dispatch-sync [:close-task])
  (run-events [[:set-active-project (js/parseInt id)]
               [:set-active-page :edit-task]]))

;; TODO: get project tasks from db
(secretary/defroute "/projects/:project-id/tasks/:task-id/edit" [project-id task-id]
  (run-events [[:set-active-project (js/parseInt project-id)]
               [:set-active-task (js/parseInt project-id) (js/parseInt task-id)]
               [:set-active-page :edit-task]]))

; ------------------------------------------------------------------------------
; History
; must be called after routes have been defined
; ------------------------------------------------------------------------------

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true))
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!))
