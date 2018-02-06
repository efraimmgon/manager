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
               [:set-active-page :home]
               [:set-title "Manager - An evidence based management system"]]))

;; create
(secretary/defroute "/projects/new" []
  (rf/dispatch-sync [:projects/close-project])
  (run-events [[:set-title "Manager - Create a project"]
               [:set-active-page :new-project]
               [:projects/set-project-path [:projects :new-project]]]))

;; read
(secretary/defroute "/projects/:id" [id]
  (run-events [[:set-title "Manager - View project"]
               [:projects/load-project (js/parseInt id)]
               [:stories/load-stories-for (js/parseInt id)]
               [:set-active-page :project]]))

;; update
(secretary/defroute "/projects/:id/edit" [id]
  (rf/dispatch-sync [:projects/close-project])
  (run-events [[:set-title "Manager - Edit project"]
               [:projects/load-project (js/parseInt id)]
               [:set-active-page :edit-project]
               [:projects/set-project-path [:projects :project]]]))

; stories ---------------------------------------------------------------------

;; create
(secretary/defroute "/projects/:project-id/stories/new" [project-id]
  (rf/dispatch-sync [:stories/close-story])
  (run-events [[:set-title "Manager - Create story"]
               [:projects/load-project (js/parseInt project-id)]
               [:set-active-page :new-story]
               [:stories/set-story-path [:stories :new-story]]]))

;; read
(secretary/defroute "/projects/:project-id/stories/:story-id"
  [project-id story-id]
  (run-events [[:set-title "Manager - View story"]
               [:projects/load-project (js/parseInt project-id)]
               [:stories/load-story-with-tasks (js/parseInt story-id)]
               [:set-active-page :story-tasks]
               [:stories/set-story-path [:stories :story]]]))


; users ------------------------------------------------------------------------

(secretary/defroute "/users" []
  (run-events [[:set-title "Manager - List users"]
               [:users/load-users]
               [:set-active-page :users/users]]))

(secretary/defroute "/users/new" []
  (run-events [[:set-title "Manager - Create user"]
               [:users/close-user]
               [:set-active-page :users/new-user]]))

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
