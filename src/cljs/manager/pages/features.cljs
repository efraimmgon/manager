(ns manager.pages.features
  (:require
   [manager.components :refer [base breadcrumbs form-group]]
   [manager.pages.components :refer [edit-project-button]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [stand-lib.components :refer [input pretty-display thead tbody textarea select]]
   [stand-lib.re-frame.utils :refer [<sub]]))

(defn form-template []
  (r/with-let [feature (rf/subscribe [:features/feature])]
    [:div.form-horizontal
     (when (:feature-id @feature)
       [form-group
        "Feature id"
        [input {:class "form-control"
                :name :features.feature.feature-id
                :type :text
                :value (:feature-id @feature)
                :disabled true}]])
     [form-group
      "Title"
      [:div.input-group
       [input {:class "form-control"
               :name :features.feature.title
               :type :text
               :value (:title @feature)
               :auto-focus (when-not (:features.feature-id @feature) true)}]
       [:div.input-group-addon "*"]]]
     [form-group
      "Description"
      [textarea {:class "form-control"
                 :name :features.feature.description
                 :value (:description @feature)}]]
     [form-group
      "Priority"
      [:div.input-group
       [select {:class "form-control"
                :name :features.feature/priority-id
                :value (:priority-id @feature)}
        (for [priority (<sub [:priorities])]
          ^{:key (:priority-id priority)}
          [:option {:value (:priority-id priority)}
           (:name priority)])]
       [:div.input-group-addon "*"]]]
     (when (:created-at @feature)
       [form-group
        "Created at"
        [input {:class "form-control"
                :name :features.feature.created-at
                :type :date
                :value (-> (:created-at @feature) (.split "T") first)
                :disabled true}]])
     (when (:updated-at @feature)
       [form-group
        "Updated at"
        [input {:class "form-control"
                :name :features.feature.updated-at
                :type :date
                :value (-> (:updated-at @feature) (.split "T") first)
                :disabled true}]])]))

(defn task-items []
  (r/with-let [tasks (rf/subscribe [:features.feature/tasks])]
    (when (seq @tasks)
      [:table.table.table-striped
       [:thead>tr
        [:th "Title"]
        [:th "Original estimate"]
        [:th "Current estimate"]
        [:th "Delete"]]
       [:tbody
        (for [task @tasks]
          (let [task-id (:task-id task)]
            ^{:key (:task-id task)}
            [:tr
             [:td [input {:class "form-control"
                          :name [:features :feature :tasks task-id :title]
                          :type :text
                          :value (:title task)}]]
             [:td [input {:class "form-control"
                          :name [:features :feature :tasks task-id :orig-est]
                          :type :number
                          :value (:orig-est task)}]]
             [:td [input {:class "form-control"
                          :name [:features :feature :tasks task-id :curr-est]
                          :type :number
                          ;; by default curr-est == orig-est
                          :value (or (:curr-est task) (:orig-est task))}]]
             [:td [:button.btn.btn-danger
                   {:on-click #(rf/dispatch [:tasks/delete-task task-id])}
                   "Del"]]]))]])))

; Load feature with tasks
; When clicked on "update" tasks with no feature-id are saved, while tasks
; with an id are updated
; Optionally I can use the on-blur event to persist changes
(defn edit-feature-page []
  (r/with-let [project (rf/subscribe [:project])
               feature (rf/subscribe [:features/feature])]
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
       [pretty-display feature]
       [:h3 "Tasks"]
       [task-items]
       [:button.btn.btn-default
        ; TODO
        {:on-click #(rf/dispatch [:features/feature-tasks-tick])}
        [:i.glyphicon.glyphicon-plus]
        " Add task"]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:features/update-feature @feature])}
         "Update"]]]]]))

(defn new-feature-page []
  (r/with-let [project (rf/subscribe [:project])
               feature (rf/subscribe [:features/feature])]
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
       [pretty-display feature]
       [:h3 "Tasks"]
       [task-items]
       [:button.btn.btn-default
        {:on-click #(rf/dispatch [:features/feature-tasks-tick])}
        [:i.glyphicon.glyphicon-plus]
        " Add task"]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:features/create-feature (:project-id @project) @feature])}
         "Create"]]]]]))

(defn new-feature-button [project]
  [:a.btn.btn-link {:href (str "/projects/" (:project-id @project)
                               "/features/new")}
   [:i.glyphicon.glyphicon-plus]
   " Create feature"])

(defn project-features-page []
  (r/with-let [project (rf/subscribe [:project])
               features (rf/subscribe [:features/features])]
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
              (:title feature)
              (if-let [len (:pending-task-count feature)]
                (str " (" len ")")
                (str " (" 0 ")"))]
             [:div.pull-right
              [:a.btn.btn-link {:href (str "/projects/" (:project-id @project)
                                           "/features/" (:feature-id feature) "/edit")}
               [:i.glyphicon.glyphicon-edit]]
              [:button.btn.btn-link {:on-click #(rf/dispatch [:features/delete-feature (:project-id @project) (:feature-id feature)])}
               [:i.glyphicon.glyphicon-remove]]]]
            [:p (:description feature)]]))]]]))
