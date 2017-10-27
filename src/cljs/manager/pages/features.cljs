(ns manager.pages.features
  (:require
   [manager.components :as c :refer [base breadcrumbs form-group input thead tbody]]
   [reagent.core :as r :refer [atom]]
   [re-frame.core :as rf]))

(defn form-template [doc]
  [:div.form-horizontal
   [form-group
    "Feature id"
    [input {:class "form-control"
            :name :feature-id
            :type :text
            :disabled true}
     doc]]
   [form-group
    "Title"
    [:div.input-group
     [input {:class "form-control"
             :name :title
             :type :text}
      doc]
     [:div.input-group-addon "*"]]]
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

(defn edit-feature-page []
  (r/with-let [project (rf/subscribe [:project])
               feature (rf/subscribe [:feature])
               doc (atom nil)]
    (reset! doc @feature)
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:title (:title @feature)
       :href (str "/projects/" (:project-id @project)
                  "/features/" (:feature-id @feature))}
      {:title "Edit", :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Edit feature"]]
      [:div.panel-body
       [form-template doc]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:edit-feature doc])}
         "Update"]]]]]))

(defn new-feature-page []
  (r/with-let [project (rf/subscribe [:project])
               doc (atom (-> {} (update :description #(or % ""))))]
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:title "New feature", :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Create feature"]]
      [:div.panel-body
       [form-template doc]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:create-feature (:project-id @project) doc])}
         "Create"]]]]]))

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
         [:a.btn.btn-link {:href (str "/projects/" (:project-id @project) "/edit")}
          [:i.glyphicon.glyphicon-edit]
          " Edit"]
         [:a.btn.btn-link {:href (str "/projects/" (:project-id @project)
                                      "/features/new")}
          [:i.glyphicon.glyphicon-plus]
          " New feature"]]]]
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
