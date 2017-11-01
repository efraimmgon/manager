(ns manager.pages.projects
  (:require
   [manager.components :as c :refer [base breadcrumbs form-group input textarea]]
   [manager.events :refer [<sub]]
   [reagent.core :as r :refer [atom]]
   [re-frame.core :as rf]))

(defn form-template []
  (r/with-let [project (rf/subscribe [:project])]
    [:div.form-horizontal
     (when (:project-id @project)
       [form-group
        "Project id"
        [input {:class "form-control"
                :name :project.project-id
                :type :text
                :value (:project-id @project)
                :disabled true}]])
     [form-group
      "Title"
      [:div.input-group
       [input {:class "form-control"
               :name :project.title
               :type :text
               :value (:title @project)}]
       [:div.input-group-addon "*"]]]
     [form-group
      "Description"
      [textarea {:class "form-control"
                 :name :project.description
                 :value (:description @project)}]]
     (when (:created-at @project)
       [form-group
        "Created at"
        [input {:class "form-control"
                :name :project.created-at
                :type :date
                :value (-> (:updated-at @project) (.split "T") first)
                :disabled true}]])
     (when (:updated-at @project)
       [form-group
        "Updated at"
        [input {:class "form-control"
                :name :project.updated-at
                :type :date
                :value (-> (:updated-at @project) (.split "T") first)
                :disabled true}]])]))

(defn new-project-page
  "Template to CREATE a project"
  []
  (r/with-let [project (rf/subscribe [:project])]
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
  (r/with-let [project (rf/subscribe [:project])]
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
  (r/with-let [projects (rf/subscribe [:projects])]
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
             [:i.glyphicon.glyphicon-remove]]]]])]]]))
