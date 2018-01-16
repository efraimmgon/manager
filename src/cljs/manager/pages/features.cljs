(ns manager.pages.features
  (:require
   [manager.components :refer [base breadcrumbs form-group]]
   [manager.pages.components :refer [edit-project-button]]
   [manager.utils :refer [done?]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [stand-lib.components :refer [pretty-display thead tbody]]
   [stand-lib.comps.forms :refer [input textarea select]]
   [stand-lib.re-frame.utils :refer [<sub]]))

(defn form-template []
  (r/with-let [feature (rf/subscribe [:features/feature])
               priorities (rf/subscribe [:priorities])]
    [:div.form-horizontal
     (when (:feature-id @feature)
       [form-group
        "Feature id"
        [input {:type :text
                :class "form-control"
                :name :features.feature/feature-id}]])
     [form-group
      "Title"
      [:div.input-group
       [input {:type :text
               :class "form-control"
               :name :features.feature/title
               :auto-focus (when-not (:features.feature-id @feature) true)}]
       [:div.input-group-addon "*"]]]
     [form-group
      "Description"
      [textarea {:class "form-control"
                 :name :features.feature/description}]]
     [form-group
      "Status"
      [:div
       (doall
         (for [[k label checked?] (<sub [:status])]
           ^{:key k}
           [:label.radio-inline
            [input {:type :radio
                    :name :features.feature/status
                    :default-checked (and (nil? (:status @feature)) checked?)
                    :value k}]
            " " label]))]]
     [form-group
      "Priority"
      [:div.input-group
       [select {:class "form-control"
                :name :features.feature/priority
                :default-value (ffirst @priorities)}
        (for [[k label] @priorities]
          ^{:key k}
          [:option {:value k}
           label])]
       [:div.input-group-addon "*"]]]
     (when (:created-at @feature)
       [form-group
        "Created at"
        [input {:type :text
                :class "form-control"
                :name :features.feature/created-at
                :disabled true}]])
     (when (:updated-at @feature)
       [form-group
        "Updated at"

        [input {:type :text
                :class "form-control"
                :name :features.feature/updated-at
                :disabled true}]])]))

(defn task-items []
  (r/with-let [tasks (rf/subscribe [:features.feature/tasks])]
    (when (seq @tasks)
      [:table.table
       [:thead>tr
        [:th "Done?"]
        [:th "Title"]
        [:th "Original estimate"]
        [:th "Current estimate"]
        [:th "Delete"]]
       [:tbody
        (for [task @tasks]
          (let [task-id (:task-id task)
                input-class (if (done? task) "form-control task-input-done" "form-control")]
            ^{:key (:task-id task)}
            [:tr {:class (when (done? task) "task-done")}
             [:td [input {:type :checkbox
                          :name [:features :feature :tasks task-id :status]
                          :value :done}]]
             [:td [input {:type :text
                          :class input-class
                          :name [:features :feature :tasks task-id :title]}]]
             [:td [input {:type :number
                          :class input-class
                          :name [:features :feature :tasks task-id :orig-est]}]]
             [:td [input {:type :number
                          :class input-class
                          :name [:features :feature :tasks task-id :curr-est]}]]
             [:td [:button.btn.btn-default
                   {:on-click #(rf/dispatch [:tasks/delete-task task-id])}
                   [:i.glyphicon.glyphicon-remove]]]]))]])))

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
                  "/features/" (:feature-id @feature))
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Edit feature"]]
      [:div.panel-body
       [form-template]
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
            {:class (when (done? feature) "list-group-item-success")}
            [:h3
             [:a {:href (str "/projects/" (:project-id @project) "/features/" (:feature-id feature))}
              (:title feature)]
             [:div.pull-right
              [:a.btn.btn-link {:href (str "/projects/" (:project-id @project)
                                           "/features/" (:feature-id feature) "/edit")}
               [:i.glyphicon.glyphicon-edit]]
              [:button.btn.btn-link {:on-click #(rf/dispatch [:features/delete-feature (:project-id @project) (:feature-id feature)])}
               [:i.glyphicon.glyphicon-remove]]]]
            [:p (:description feature)]]))]]]))
