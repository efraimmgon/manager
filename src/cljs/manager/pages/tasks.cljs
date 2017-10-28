(ns manager.pages.tasks
  (:require
   [manager.components :as c :refer
    [base breadcrumbs form-group input select textarea tbody thead-editable]]
   [manager.events :refer [<sub]]
   [reagent.core :as r :refer [atom]]
   [re-frame.core :as rf]))

(defn calc-velocity [doc]
  (and ;(= 2 (:status-id @doc))
       (:curr-est @doc)
       (pos? (:curr-est @doc))
       (/ (:orig-est @doc) (:curr-est @doc))))

(defn form-template [doc]
  [:div.form-horizontal
   (when (:task-id @doc)
     [form-group
      "Task id"
      [input {:class "form-control"
              :name :task-id
              :type :number
              :disabled true}
       doc]])
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
    [textarea {:class "form-control"
               :name :description}
     doc]]
   [form-group
    "Original estimate"
    [:div.input-group
     [input {:class "form-control"
             :name :orig-est
             :type :number}
      doc]
     [:div.input-group-addon "*"]]]
   (when (:task-id @doc)
     [form-group
      "Current estimate"
      [input {:class "form-control"
              :name :curr-est
              :type :number
              :placeholder "Defaults to: original estimate"}
       doc]])
   (when (:task-id @doc)
     [form-group
      "Velocity"
      [input {:class "form-control"
              :value (calc-velocity doc)
              :name :velocity
              :type :number
              :placeholder "Velocity = original estimate / current estimate."
              :disabled true}
       doc]])
   [form-group
    "Priority"
    [:div.input-group
     [select {:class "form-control"
              :name :priority-id}
      doc
      (for [priority (<sub [:priorities])]
        ^{:key (:priority-id priority)}
        [:option {:value (:priority-id priority)}
         (:name priority)])]
     [:div.input-group-addon "*"]]]
   [form-group
    "Time Elapsed"
    [input {:class "form-control"
            :name :elapsed
            :type :number
            :placeholder "Defaults to: 0"}
     doc]]
   (when (:task-id @doc)
     [form-group
      "Time Remaining"
      [input {:class "form-control"
              :name :remain
              :type :number
              :placeholder "Defaults to: current estimate - time elapsed"
              :disabled true}
       doc]])
   (when (:task-id @doc)
     [form-group
      "Created at"
      [input {:class "form-control"
              :name :created-at
              :type :date
              :disabled true}
       doc]])
   (when (:task-id @doc)
     [form-group
      "Updated at"
      [input {:class "form-control"
              :name :updated-at
              :type :date
              :disabled true}
       doc]])
   [form-group
    "Status"
    [:div
     (for [status (<sub [:status])]
       ^{:key (:status-id status)}
       [:label.checkbox-inline
        [input {:name :status-id
                :type :radio
                :value (:status-id status)}
         doc]
        " " (:name status)])]]])

(defn new-task-page []
  (r/with-let [project (rf/subscribe [:project])
               feature (rf/subscribe [:feature])
               doc (atom {})]
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:href (str "/projects/" (:project-id @project) "/features/" (:feature-id @feature))
       :title (:title @feature)}
      {:title "New task", :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Create task"]]
      [:div.panel-body
       [form-template doc]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:create-task (:project-id @project) (:feature-id @feature) doc])}
         "Create"]]]]]))

(defn edit-task-page []
  (r/with-let [project (rf/subscribe [:project])
               feature (rf/subscribe [:feature])
               task (rf/subscribe [:task])
               doc (atom nil)]
    (reset! doc (assoc @task :feature-id (:feature-id @feature)))
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:href (str "/projects/" (:project-id @project) "/features/" (:feature-id @feature))
       :title (:title @feature)}
      {:title (:title @task)
       :href (str "/projects/" (:project-id @project)
                  "/features/" (:feature-id @feature)
                  "/tasks/" (:task-id @task))}
      {:title "Edit", :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Edit task"]]
      [:div.panel-body
       [c/pretty-display "doc" doc]
       [form-template doc]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:edit-task (:project-id @project) doc])}
         "Update"]]]]]))

(defn task-page []
  (r/with-let [project (rf/subscribe [:project])
               feature (rf/subscribe [:feature])
               task (rf/subscribe [:task])]
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:href (str "/projects/" (:project-id @project)
                  "/features/" (:feature-id @feature))
       :title (:title @feature)}
      {:title (:title @task)
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading>h2
       (:title @task)]
      [:ul.list-group
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 [:h4 "id"]]
         [:div.col-md-10
          (:task-id @task)]]]
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 [:h4 "Description"]]
         [:div.col-md-10
          (:description @task)]]]
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 [:h4 "Orig est"]]
         [:div.col-md-10
          (:orig-est @task)]]]
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 [:h4 "Curr est"]]
         [:div.col-md-10
          (:curr-est @task)]]]
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 [:h4 "Priority"]]
         [:div.col-md-10
          (:priority-name @task)]]]
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 [:h4 "Elapsed"]]
         [:div.col-md-10
          (:elapsed @task)]]]
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 [:h4 "Remain"]]
         [:div.col-md-10
          (:remain @task)]]]
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 [:h4 "Status"]]
         [:div.col-md-10
          (:status-name @task)]]]]]]))

(defn feature-tasks-page
  "Template listing all the feature's tasks"
  []
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
                                      "/features/" (:feature-id @feature) "/edit")}
          [:i.glyphicon.glyphicon-edit]
          " Edit"]
         [:a.btn.btn-link {:href (str "/projects/" (:project-id @project)
                                      "/features/" (:feature-id @feature)
                                      "/tasks/new")}
          [:i.glyphicon.glyphicon-plus]
          " New task"]]]]
      [:ul.list-group
       (when-not (seq @tasks)
         [:li.list-group-item
          "No tasks yet."])
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
