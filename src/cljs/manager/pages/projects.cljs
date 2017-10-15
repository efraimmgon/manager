(ns manager.pages.projects
  (:require
   [manager.components :as c :refer [base breadcrumbs form-group]]
   [reagent.core :as r :refer [atom]]
   [reagent-forms.core :refer [bind-fields]]
   [re-frame.core :as rf]))

(def form-template
  [:div.form-horizontal
   (form-group
    "Project id"
    [:input.form-control
     {:field :text, :id :project-id, :disabled true}])
   (form-group
    "Title"
    [:input.form-control
     {:field :text, :id :title}])
   (form-group
    "Description"
    [:input.form-control
     {:field :text, :id :description}])])

(defn edit-project-page []
  (r/with-let [project (rf/subscribe [:project])
               doc (atom @project)]
    [base
     [breadcrumbs
      {:title (or (:title @project) "New project")
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       (if @project
         [:h2 "Edit project"]
         [:h2 "Create project"])]
      [:div.panel-body
       [bind-fields
        form-template
        doc]
       [:div.col-sm-offset-2.col-sm-10
        (if @project
          [:button.btn.btn-primary
           {:on-click #(rf/dispatch [:edit-project doc])}
           "Save"]
          [:button.btn.btn-primary
           {:on-click #(rf/dispatch [:create-project doc])}
           "Create"])]]]]))

(defn projects-page []
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
