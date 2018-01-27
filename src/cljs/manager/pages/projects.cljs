(ns manager.pages.projects
  (:require
   [manager.components :refer [base breadcrumbs form-group]]
   [reagent.core :as r :refer [atom]]
   [re-frame.core :as rf]
   [stand-lib.re-frame.utils :refer [<sub]]
   [stand-lib.comps.forms :refer [input textarea]]
   [stand-lib.utils.forms :refer
    [handle-change-at]]))

(defn form-template []
  (r/with-let [project (rf/subscribe [:projects/project])]
    [:div.form-horizontal
     (when (:project-id @project)
       [form-group
        "Project id"
        [:input {:class "form-control"
                 :type :text
                 :value (:project-id @project)
                 :disabled true}]])
     [form-group
      "Title"
      [:div.input-group
       [input {:type :text
               :class "form-control"
               :name :projects.project/title
               :auto-focus true}]
       [:div.input-group-addon "*"]]]
     [form-group
      "Description"
      [textarea {:class "form-control"
                 :name :projects.project/description}]]
     (when (:created-at @project)
       [form-group
        "Created at"
        [:input {:class "form-control"
                 :type :text
                 :value (:created-at @project)
                 :disabled true}]])
     (when (:updated-at @project)
       [form-group
        "Updated at"
        [:input {:class "form-control"
                 :type :text
                 :value (:updated-at @project)
                 :disabled true}]])]))

(defn new-project-page
  "Template to CREATE a project"
  []
  (r/with-let [project (rf/subscribe [:projects/project])]
    [base
     [breadcrumbs
      {:title "New project"
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Create project"]]
      [:div.panel-body
       [form-template]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:create-project @project])}
         "Create"]]]]]))

(defn edit-project-page
  "Template to EDIT a project"
  []
  (r/with-let [project (rf/subscribe [:projects/project])]
    [base
     [breadcrumbs
      {:title (:title @project),
       :href (str "/projects/" (:project-id @project))}
      {:title "Edit", :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Edit project"]]
      [:div.panel-body
       [form-template]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:edit-project @project])}
         "Update"]]]]]))

(defn projects-page
  "Template to LIST all projects"
  []
  (r/with-let [projects (rf/subscribe [:projects/all])]
    [base
     [breadcrumbs]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Projects"
        [:div.pull-right
         [:a.btn.btn-link {:href (str "/projects/new")}
          [:i.glyphicon.glyphicon-plus]
          " New project"]]]]
      [:ul.list-group
       (when-not (seq @projects)
         [:li.list-group-item "No projects yet."])
       (for [project @projects]
         ^{:key (:project-id project)}
         [:li.list-group-item
          [:h3
           [:a {:href (str "/projects/" (:project-id project))}
            (:title project)]
           [:div.pull-right
            [:a.btn.btn-link {:href (str "/projects/" (:project-id project) "/edit")}
             [:i.glyphicon.glyphicon-edit]]
            [:button.btn.btn-link {:on-click #(rf/dispatch [:delete-project (:project-id project)])}
             [:i.glyphicon.glyphicon-remove]]]]
          [:p (:description project)]])]]]))
