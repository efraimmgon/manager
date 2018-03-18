(ns manager.views
  (:require
   [reagent.core :as r :refer [atom]]
   [re-frame.core :as rf]
   [manager.components :refer [base breadcrumbs]]
   [manager.events]
   [manager.pages.stories :as stories]
   [manager.pages.projects :as projects]
   [manager.pages.tasks :as tasks]
   [manager.pages.users :as users]
   [stand-lib.components :refer [thead tbody]]
   [stand-lib.re-frame.utils :refer [<sub]]))

(defn navbar []
  (r/with-let [project (rf/subscribe [:projects/project])]
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
        [:li>a
         {:href "/users"}
         [:i.glyphicon.glyphicon-user]
         " Users"]
        [:li>a
         {:href "/projects/new"}
         [:i.glyphicon.glyphicon-plus]
         " Create project"]
        (when @project
          [:li
           [stories/new-story-button project]])]]]]))

(def pages
  {:home #'projects/projects-page
   :new-project #'projects/new-project-page
   :edit-project #'projects/edit-project-page
   :project #'stories/project-stories-page
   :project-tasks #'tasks/project-tasks-page
   :new-story #'stories/new-story-page
   :edit-story #'stories/edit-story-page
   :story-tasks #'stories/edit-story-page
   :new-task #'tasks/new-task-page
   :edit-task #'tasks/edit-task-page
   :users/users #'users/list-users-page
   :users/new-user #'users/new-user-page
   :users/edit-user #'users/edit-user-page})

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
      (into
        [:ul]
        (for [err error]
          [:li (str err)]))]
     [:div
      [:button.btn.btn-sm.btn-danger
       {:on-click #(rf/dispatch [:set-error nil])}
       "OK"]]]))

(defn page []
  [:div
   [error-modal]
   [navbar]
   [(pages @(rf/subscribe [:page]))]])
