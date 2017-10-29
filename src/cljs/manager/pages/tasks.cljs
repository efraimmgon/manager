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

(defn form-template []
  (r/with-let [task (rf/subscribe [:task])]
    [:div.form-horizontal
     (when (:task-id @task)
       [form-group
        "Task id"
        [input {:class "form-control"
                :name :task.task-id
                :type :number
                :value (:task-id @task)
                :disabled true}]])
     [form-group
      "Title"
      [:div.input-group
       [input {:class "form-control"
               :name :task.title
               :type :text
               :value (:title @task)}]
       [:div.input-group-addon "*"]]]
     [form-group
      "Description"
      [textarea {:class "form-control"
                 :name :task.description
                 :value (:description @task)}]]
     [form-group
      "Original estimate"
      [:div.input-group
       [input {:class "form-control"
               :name :task.orig-est
               :type :number
               :value (:orig-est @task)}]
       [:div.input-group-addon "*"]]]
     (when (:task-id @task)
       [form-group
        "Current estimate"
        [input {:class "form-control"
                :name :task.curr-est
                :type :number
                :value (:curr-est @task)
                :placeholder "Defaults to: original estimate"}]])
     (when (:task-id @task)
       [form-group
        "Velocity"
        [input {:class "form-control"
                :value (calc-velocity task)
                :name :task.velocity
                :type :number
                :placeholder "Velocity = original estimate / current estimate."
                :disabled true}]])
     [form-group
      "Priority"
      [:div.input-group
       [select {:class "form-control"
                :name :task.priority-id
                :value (:priority-id @task)}
        (for [priority (<sub [:priorities])]
          ^{:key (:priority-id priority)}
          [:option {:value (:priority-id priority)}
           (:name priority)])]
       [:div.input-group-addon "*"]]]
     [form-group
      "Time Elapsed"
      [input {:class "form-control"
              :name :task.elapsed
              :type :number
              :value (:elapsed @task)
              :placeholder "Defaults to: 0"}]]
     (when (:task-id @task)
       [form-group
        "Time Remaining"
        [input {:class "form-control"
                :name :task.remain
                :type :number
                :value (:remain @task)
                :placeholder "Defaults to: current estimate - time elapsed"
                :disabled true}]])
     (when (:task-id @task)
       [form-group
        "Created at"
        [input {:class "form-control"
                :name :task.created-at
                :type :date
                :value (-> (:created-at @task) (.split "T") first)
                :disabled true}]])
     (when (:task-id @task)
       [form-group
        "Updated at"
        [input {:class "form-control"
                :name :task.updated-at
                :type :date
                :value (-> (:updated-at @task) (.split "T") first)
                :disabled true}]])
     [form-group
      "Status"
      [:div
       (doall
         (for [status (<sub [:status])]
           ^{:key (:status-id status)}
           [:label.checkbox-inline
            [input {:name :task.status-id
                    :type :radio
                    :value (:status-id status)
                    :checked (= (:status-id status) (:status-id @task))}]
            " " (:name status)]))]]]))

(defn new-task-page []
  (r/with-let [project (rf/subscribe [:project])
               feature (rf/subscribe [:feature])
               task (rf/subscribe [:task])]
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
       [form-template]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:create-task (:project-id @project) (:feature-id @feature) @task])}
         "Create"]]]]]))

(defn edit-task-page []
  (r/with-let [project (rf/subscribe [:project])
               feature (rf/subscribe [:feature])
               task (rf/subscribe [:task])]
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:href (str "/projects/" (:project-id @project) "/features/" (:feature-id @feature))
       :title (:title @feature)}
      {:title (:title @task)
       :href (str "/projects/" (:project-id @project)
                  "/features/" (:feature-id @feature)
                  "/tasks/" (:task-id @task))
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Edit task"]]
      [:div.panel-body
       [form-template]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:edit-task (:project-id @project) (assoc @task :feature-id (:feature-id @feature))])}
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
                             "/tasks/" (:task-id task) "/edit")}
              (:title task)]
             [:div.pull-right
              (:priority-id task)
              [:a.btn.btn-link {:href (str "/projects/" (:project-id @project)
                                           "/features/" (:feature-id @feature)
                                           "/tasks/" (:task-id task) "/edit")}
               [:i.glyphicon.glyphicon-edit]]
              [:button.btn.btn-link {:on-click #(rf/dispatch [:delete-task (:task-id task)])}
               [:i.glyphicon.glyphicon-remove]]]]
            [:p (:description task)]]))]]]))
