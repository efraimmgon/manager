(ns manager.pages.features
  (:require
   [manager.components :as c :refer [base breadcrumbs form-group input thead tbody textarea]]
   [manager.events :refer [<sub]]
   [manager.pages.components :refer [edit-project-button]]
   [reagent.core :as r :refer [atom]]
   [re-frame.core :as rf]))

(defn form-template []
  (r/with-let [feature (rf/subscribe [:feature])]
    [:div.form-horizontal
     (when (:feature-id @feature)
       [form-group
        "Feature id"
        [input {:class "form-control"
                :name :feature.feature-id
                :type :text
                :value (:feature-id @feature)
                :disabled true}]])
     [form-group
      "Title"
      [:div.input-group
       [input {:class "form-control"
               :name :feature.title
               :type :text
               :value (:title @feature)
               :auto-focus (when-not (:feature-id @feature) true)}]
       [:div.input-group-addon "*"]]]
     [form-group
      "Description"
      [textarea {:class "form-control"
                 :name :feature.description
                 :value (:description @feature)}]]
     (when (:created-at @feature)
       [form-group
        "Created at"
        [input {:class "form-control"
                :name :feature.created-at
                :type :date
                :value (-> (:created-at @feature) (.split "T") first)
                :disabled true}]])
     (when (:updated-at @feature)
       [form-group
        "Updated at"
        [input {:class "form-control"
                :name :feature.updated-at
                :type :date
                :value (-> (:updated-at @feature) (.split "T") first)
                :disabled true}]])]))

(defn edit-feature-page []
  (r/with-let [project (rf/subscribe [:project])
               feature (rf/subscribe [:feature])]
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:title (:title @feature)
       :href (str "/projects/" (:project-id @project)
                  "/features/" (:feature-id @feature))}
      {:title "Edit" :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Edit feature"]]
      [:div.panel-body
       [form-template]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:edit-feature @feature])}
         "Update"]]]]]))

(defn new-feature-page []
  (r/with-let [project (rf/subscribe [:project])]
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:title "New feature", :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Create feature"]]
      [:div.panel-body
       [form-template]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:create-feature (:project-id @project) (<sub [:feature])])}
         "Create"]]]]]))

(defn new-feature-button [project]
  [:a.btn.btn-link {:href (str "/projects/" (:project-id @project)
                               "/features/new")}
   [:i.glyphicon.glyphicon-plus]
   " New feature"])

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
        [edit-project-button project]
        [:div.pull-right
         [new-feature-button project]]]]
      [:ul.list-group
       (when-not (seq @features)
         [:li.list-group-item "No features yet."])
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
