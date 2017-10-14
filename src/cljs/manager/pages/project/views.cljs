(ns manager.pages.project.views
  (:require
   [manager.components :as c :refer [base breadcrumbs thead tbody]]
   [manager.pages.task :as task]
   [reagent.core :as r :refer [atom]]
   [reagent-forms.core :refer [bind-fields]]
   [re-frame.core :as rf]))

(defn form-group [label & input]
  [:div.form-group
   [:label.col-sm-2.control-label label]
   (into
    [:div.col-sm-10]
    input)])

(def feature-form-template
  [:div.form-horizontal
   (form-group
    "Feature id"
    [:input.form-control
     {:field :text, :id :feature-id, :disabled true}])
   (form-group
    "Title"
    [:input.form-control
     {:field :text, :id :title}])
   (form-group
    "Description"
    [:input.form-control
     {:field :text, :id :description}])])

; edit: dispatch fn
; create: dispatch fn
(defn edit-feature-page []
  (r/with-let [project (rf/subscribe [:project])
               feature (rf/subscribe [:feature])
               doc (atom @feature)]
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:title (or (:title @feature) "New feature")
       :active? true}]

     [:div.panel.panel-default
      [:div.panel-heading
       (if @project
         [:h2 "Edit feature"]
         [:h2 "Create feature"])]
      [:div.panel-body
       [bind-fields
        feature-form-template
        doc]
       [:div.col-sm-offset-2.col-sm-10
        (if @project
          [:button.btn.btn-primary
           {:on-click #(rf/dispatch [:edit-feature (:feature-id @feature) @doc])}
           "Save"]
          [:button.btn.btn-primary
           {:on-click #(rf/dispatch [:create-project (:project-id @project) @doc])}
           "Create"])]]]]))

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
           {:on-click #(rf/dispatch [:edit-project @doc])}
           "Save"]
          [:button.btn.btn-primary
           {:on-click #(rf/dispatch [:create-project @doc])}
           "Create"])]]]]))

(defn feature-tasks-page []
  (r/with-let [project (rf/subscribe [:project])
               feature (rf/subscribe [:feature])
               tasks (rf/subscribe [:tasks])]
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:href (str "/projects/" (:project-id @project) "/features/" (:feature-id @feature))
       :title (:title @feature)
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 (:title @feature)
        [:div.pull-right
         [:a.btn.btn-link {:href (str "/projects/" (:project-id @project)
                                      "/features/" (:feature-id @feature)
                                      "/tasks/new")}
          [:i.glyphicon.glyphicon-plus]
          " New task"]]]]
      [:ul.list-group
       (doall
         (for [task @tasks]
           ^{:key (:task-id task)}
           [:li.list-group-item
            [:h3
             [:a {:href (str "/projects/" (:project-id @project)
                             "/features/" (:feature-id @feature)
                             "/tasks/" (:task-id task))}
              (:title task)]
             [:div.pull-right
              (:priority task)
              [:a.btn.btn-link {:href (str "/projects/" (:project-id @project)
                                           "/features/" (:feature-id @feature)
                                           "/tasks/" (:task-id task) "/edit")}
               [:i.glyphicon.glyphicon-edit]]
              [:button.btn.btn-link {:on-click #(rf/dispatch [:delete-task (:task-id task)])}
               [:i.glyphicon.glyphicon-remove]]]]
            [:p (:description task)]]))]]]))

(defn project-features-page []
  (r/with-let [project (rf/subscribe [:project])
               features (rf/subscribe [:features])]
    [base
     [breadcrumbs
      {:title (:title @project)
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 (:title @project)
        [:div.pull-right
         [:a.btn.btn-link {:href (str "/projects/" (:project-id @project)
                                      "/features/new")}
          [:i.glyphicon.glyphicon-plus]
          " New feature"]]]]
      [:ul.list-group
       (doall
         (for [feature @features]
           ^{:key (:feature-id feature)}
           [:li.list-group-item
            [:h3
             [:a {:href (str "/projects/" (:project-id @project) "/features/" (:feature-id feature))}
              (:title feature)]
             [:div.pull-right
              [:a.btn.btn-link {:href (str "/projects/" (:project-id @project)
                                           "/features/" (:feature-id feature) "/edit")}
               [:i.glyphicon.glyphicon-edit]]
              [:button.btn.btn-link {:on-click #(rf/dispatch [:delete-feature (:feature-id feature)])}
               [:i.glyphicon.glyphicon-remove]]]]]))]]]))

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
