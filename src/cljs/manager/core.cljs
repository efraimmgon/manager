(ns manager.core
  (:require
   [reagent.core :as r :refer [atom]]
   [re-frame.core :as rf]
   [manager.ajax :refer [load-interceptors!]]
   [manager.events :refer [<sub]]
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
  (r/with-let [project (rf/subscribe [:project])]
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
        (when @project
          [:li>a
           {:href (str "/projects/" (:project-id @project) "/tasks/unfineshed")}
           [:i.glyphicon.glyphicon-th-list]
           " List pending tasks"])

        [:li [:a {:href "/projects/new"} "Create project"]]]]]]))

(def pages
  {:home #'projects/projects-page
   :new-project #'projects/new-project-page
   :edit-project #'projects/edit-project-page
   :project #'features/project-features-page
   :project-tasks #'tasks/project-tasks-page
   :new-feature #'features/new-feature-page
   :edit-feature #'features/edit-feature-page
   :feature-tasks #'tasks/feature-tasks-page
   :new-task #'tasks/new-task-page
   :edit-task #'tasks/edit-task-page})

(defn modal [header body footer]
  [:div
   [:div.modal-dialog
    [:div.modal-content
     [:div.modal-header [:h3 header]]
     [:div.modal-body body]
     [:div.modal-footer
      [:div.bootstrap-dialog-footer
       footer]]]]
   [:div.modal-backdrop.fade.in]])

(defn error-modal []
  (when-let [error (<sub [:error])]
    [modal
     "An error has occured"
     [:div.alert.bg-danger
      [:ul
       (for [err error]
         [:li (str err)])]]
     [:div
      [:button.btn.btn-sm.btn-danger
       {:on-click #(rf/dispatch [:set-error nil])}
       "OK"]]]))

(defn page []
  [:div
   [error-modal]
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
