(ns manager.core
  (:require
   [reagent.core :as r :refer [atom]]
   [re-frame.core :as rf]
   [manager.ajax :refer [load-interceptors!]]
   [manager.events]
   [manager.routes :refer [hook-browser-navigation!]]
   ;;; Views
   [manager.components :refer [base breadcrumbs thead tbody]]
   [manager.pages.task :as task]))

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
      [:li [:a {:href "#"} "Link"]]]]]])

(defn project-page []
  (r/with-let [project (rf/subscribe [:project])]
    [base
     [breadcrumbs
      [(str "/projects/" (:id @project)) (:title @project) :active]]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2
        [:a {:href (str "/projects/" (:id @project))}
         (:title @project)]
        " "
        [:a.btn.btn-primary
         {:href (str "/projects/" (:id @project) "/tasks/new")}
         [:i.glyphicon.glyphicon-plus] " Add new"]]]
      [:div.panel-body
       [task/tasks project]]]]))

(defn home-page []
  (r/with-let [projects (rf/subscribe [:projects])]
    [base
     [breadcrumbs]
     (for [project (vals @projects)]
       ^{:key (:id project)}
       [:div.row
        [:div.col-md-12
         [:div.panel.panel-default
          [:div.panel-heading
           [:h2
            [:a {:href (str "/projects/" (:id project))}
             (:title project)]]]]]])]))

(def pages
  {:home #'home-page
   :project #'project-page
   :edit-task #'task/edit-task-page})

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
