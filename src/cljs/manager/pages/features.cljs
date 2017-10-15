(ns manager.pages.features
  (:require
   [manager.components :as c :refer [base breadcrumbs form-group thead tbody]]
   [reagent.core :as r :refer [atom]]
   [reagent-forms.core :refer [bind-fields]]
   [re-frame.core :as rf]))

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
