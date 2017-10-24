(ns manager.pages.projects
  (:require
   [manager.components :as c :refer [base breadcrumbs form-group input]]
   [reagent.core :as r :refer [atom]]
   [re-frame.core :as rf]))

(defn form-template [doc]
  [:div.form-horizontal
   [form-group
    "Project id"
    [input {:class "form-control"
            :name :project-id
            :type :text
            :disabled true}
     doc]]
   [form-group
    "Title"
    [input {:class "form-control"
             :name :title
             :type :text}
     doc]]
   [form-group
    "Description"
    [input {:class "form-control"
            :name :description
            :type :text}
     doc]]
   [form-group
    "Created at"
    [input {:class "form-control"
            :name :created-at
            :type :date
            :disabled true}
     doc]]
   [form-group
    "Updated at"
    [input {:class "form-control"
            :name :updated-at
            :type :date
            :disabled true}
     doc]]])

(defn new-project-page
  "Template to CREATE a project"
  []
  (r/with-let [project (rf/subscribe [:project])
               doc (atom {})]
    [base
     [breadcrumbs
      {:title "New project"
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Create project"]]
      [:div.panel-body
       [form-template doc]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:create-project doc])}
         "Create"]]]]]))

(defn edit-project-page
  "Template to EDIT a project"
  []
  (r/with-let [project (rf/subscribe [:project])
               doc (atom nil)]
    (reset! doc @project)
    [base
     [breadcrumbs
      {:title (:title @project),
       :href (str "/projects/" (:project-id @project))}
      {:title "Edit", :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Edit project"]]
      [:div.panel-body
       [form-template doc]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:edit-project doc])}
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
