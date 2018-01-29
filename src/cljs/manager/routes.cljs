(ns manager.routes
  (:require
   [accountant.core :as accountant]
   [re-frame.core :as rf]
   [secretary.core :as secretary]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType])
  (:import goog.History))

(defn run-events [events]
  (rf/dispatch [:update-history (-> js/window .-location .-pathname)])
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
  (run-events [[:projects/load-projects]
               [:set-active-page :home]]))

;; create
(secretary/defroute "/projects/new" []
  (rf/dispatch-sync [:projects/close-project])
  (run-events [[:set-active-page :new-project]]))

;; read
(secretary/defroute "/projects/:id" [id]
  (run-events [[:projects/load-project (js/parseInt id)]
               [:stories/load-stories-for (js/parseInt id)]
               [:set-active-page :project]]))

(secretary/defroute "/projects/:id/tasks/unfineshed" [id]
  (run-events [[:projects/load-project (js/parseInt id)]
               [:projects/load-project-tasks (js/parseInt id)]
               [:set-active-page :project-tasks]]))

;; update
(secretary/defroute "/projects/:id/edit" [id]
  (rf/dispatch-sync [:projects/close-project])
  (run-events [[:projects/load-project (js/parseInt id)]
               [:set-active-page :edit-project]]))

;; history
(secretary/defroute "/projects/:id/history" [id]
  (run-events [[:projects/load-project (js/parseInt id)]
               [:load-recently-updated-tasks-by-project (js/parseInt id)]
               [:set-active-page :project-tasks]]))

; stories ---------------------------------------------------------------------

;; create
(secretary/defroute "/projects/:project-id/stories/new" [project-id]
  (rf/dispatch-sync [:stories/close-story])
  (run-events [[:projects/load-project (js/parseInt project-id)]
               [:set-active-page :new-story]]))

;; read
(secretary/defroute "/projects/:project-id/stories/:story-id"
  [project-id story-id]
  (run-events [[:projects/load-project (js/parseInt project-id)]
               [:stories/load-story-with-tasks (js/parseInt story-id)]
               [:set-active-page :story-tasks]]))

;; update
(secretary/defroute "/projects/:project-id/stories/:story-id/edit"
  [project-id story-id]
  (run-events [[:projects/load-project (js/parseInt project-id)]
               [:stories/load-story (js/parseInt story-id)]
               [:set-active-page :edit-story]]))

; tasks ------------------------------------------------------------------------

;; create
(secretary/defroute "/projects/:project-id/stories/:story-id/tasks/new"
  [project-id story-id task-id]
  (rf/dispatch-sync [:close-task])
  (run-events [[:projects/load-project (js/parseInt project-id)]
               [:stories/load-story (js/parseInt story-id)]
               [:set-active-page :new-task]]))

;; read
(secretary/defroute "/projects/:project-id/stories/:story-id/tasks/:task-id"
  [project-id story-id task-id]
  (run-events [[:projects/load-project (js/parseInt project-id)]
               [:stories/load-story (js/parseInt story-id)]
               [:load-task (js/parseInt task-id)]
               [:set-active-page :task]]))

;; update
(secretary/defroute "/projects/:project-id/stories/:story-id/tasks/:task-id/edit"
  [project-id story-id task-id]
  (run-events [[:projects/load-project (js/parseInt project-id)]
               [:stories/load-story (js/parseInt story-id)]
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
