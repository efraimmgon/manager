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

; projects ---------------------------------------------------------------------

(secretary/defroute "/" []
  (run-events [[:load-projects]
               [:set-active-page :home]]))

;; create
(secretary/defroute "/projects/new" []
  (rf/dispatch-sync [:close-project])
  (run-events [[:set-active-page :new-project]]))

;; read
(secretary/defroute "/projects/:id" [id]
  (run-events [[:load-project (js/parseInt id)]
               [:load-features-for (js/parseInt id)]
               [:set-active-page :project]]))

;; update
(secretary/defroute "/projects/:id/edit" [id]
  (rf/dispatch-sync [:close-project])
  (run-events [[:load-project (js/parseInt id)]
               [:set-active-page :edit-project]]))

; features ---------------------------------------------------------------------

;; create
(secretary/defroute "/projects/:project-id/features/new" [project-id]
  (rf/dispatch-sync [:close-feature])
  (run-events [[:load-project (js/parseInt project-id)]
               [:set-active-page :new-feature]]))

;; read
(secretary/defroute "/projects/:project-id/features/:feature-id"
  [project-id feature-id]
  (run-events [[:load-project (js/parseInt project-id)]
               [:load-feature (js/parseInt feature-id)]
               [:load-tasks-for (js/parseInt feature-id)]
               [:set-active-page :feature-tasks]]))

;; update
(secretary/defroute "/projects/:project-id/features/:feature-id/edit"
  [project-id feature-id]
  (run-events [[:load-project (js/parseInt project-id)]
               [:load-feature (js/parseInt feature-id)]
               [:set-active-page :edit-feature]]))

; tasks ------------------------------------------------------------------------

;; create
(secretary/defroute "/projects/:project-id/features/:feature-id/tasks/new"
  [project-id feature-id task-id]
  (rf/dispatch-sync [:close-task])
  (run-events [[:load-project (js/parseInt project-id)]
               [:load-feature (js/parseInt feature-id)]
               [:set-active-page :new-task]]))

;; read
(secretary/defroute "/projects/:project-id/features/:feature-id/tasks/:task-id"
  [project-id feature-id task-id]
  (run-events [[:load-project (js/parseInt project-id)]
               [:load-feature (js/parseInt feature-id)]
               [:load-task (js/parseInt task-id)]
               [:set-active-page :task]]))

;; update
(secretary/defroute "/projects/:project-id/features/:feature-id/tasks/:task-id/edit"
  [project-id feature-id task-id]
  (run-events [[:load-project (js/parseInt project-id)]
               [:load-feature (js/parseInt feature-id)]
               [:load-task (js/parseInt task-id)]
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
