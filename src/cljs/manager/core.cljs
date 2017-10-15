(ns manager.core
  (:require
   [reagent.core :as r :refer [atom]]
   [re-frame.core :as rf]
   [manager.ajax :refer [load-interceptors!]]
   [manager.events]
   [manager.routes :refer [hook-browser-navigation!]]
   ;;; Views
   [manager.components :refer [base breadcrumbs thead tbody]]
   [manager.pages.features :as features]
   [manager.pages.projects :as projects]
   [manager.pages.tasks :as tasks]))

; ------------------------------------------------------------------------------
; Utils
; ------------------------------------------------------------------------------

(defn index-of [s v]
  (loop [idx 0 items s]
    (cond
      (empty? items) nil
      (= v (first items)) idx
      :else (recur (inc idx) (rest items)))))

; ------------------------------------------------------------------------------
; Components
; ------------------------------------------------------------------------------

(defn navbar []
  [:nav.navbar.navbar-inverse
   [:div.container-fluid
    [:div.navbar-header
     [:button.navbar-toggle.collapsed
      {:aria-expanded "false",
       :data-target "#bs-example-navbar-collapse-1",
       :data-toggle "collapse",
       :type "button"}
      [:span.sr-only "Toggle navigation"]
      [:span.icon-bar]
      [:span.icon-bar]
      [:span.icon-bar]]
     [:a.navbar-brand {:href "/"} "Manager"]]
    [:div#bs-example-navbar-collapse-1.collapse.navbar-collapse
     [:ul.nav.navbar-nav.navbar-right
      [:li [:a {:href "/projects/new"} "Create project"]]]]]])

(def pages
  {:home #'projects/projects-page
   :edit-project #'projects/edit-project-page
   :project #'features/project-features-page
   :edit-feature #'features/edit-feature-page
   :feature-tasks #'tasks/feature-tasks-page
   :task #'tasks/task-page
   :edit-task #'tasks/edit-task-page})

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]])

; ------------------------------------------------------------------------------
; Initialize app
; ------------------------------------------------------------------------------

(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (hook-browser-navigation!)
  (mount-components))
