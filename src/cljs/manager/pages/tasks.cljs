(ns manager.pages.tasks
  (:require
   [manager.components :refer [base breadcrumbs form-group]]
   [manager.pages.components :refer [edit-project-button]]
   [reagent.core :as r :refer [atom]]
   [re-frame.core :as rf]
   [stand-lib.components :refer [tbody]]
   [stand-lib.re-frame.utils :refer [<sub]]))

(declare input select textarea)

(defn calc-velocity [doc]
  (and ;(= 2 (:status @doc))
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
               :value (:title @task)
               :auto-focus (when-not (:task-id @task) true)}]
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
                :name :task.priority-idx
                :value (:priority-idx @task)}
        (for [priority (<sub [:priorities])]
          ^{:key (:priority-idx priority)}
          [:option {:value (:priority-idx priority)}
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
           ^{:key (:status status)}
           [:label.checkbox-inline
            (when-not (:status @task)
              (when (= (:name status) "pending")
                (rf/dispatch [:update-state [:task :status] (:status status)])))
            [input {:name :task.status
                    :type :radio
                    :value (:status status)
                    :checked (= (:status status) (:status @task))}]
            " " (:name status)]))]]]))

(defn new-task-page []
  (r/with-let [project (rf/subscribe [:project])
               story (rf/subscribe [:story])
               task (rf/subscribe [:task])]
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:href (str "/projects/" (:project-id @project) "/stories/" (:story-id @story))
       :title (:title @story)}
      {:title "New task", :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Create task"]]
      [:div.panel-body
       [form-template]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:create-task (:project-id @project) (:story-id @story) @task])}
         "Create"]]]]]))

(defn edit-task-page []
  (r/with-let [project (rf/subscribe [:project])
               story (rf/subscribe [:story])
               task (rf/subscribe [:task])]
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:href (str "/projects/" (:project-id @project) "/stories/" (:story-id @story))
       :title (:title @story)}
      {:title (:title @task)
       :href (str "/projects/" (:project-id @project)
                  "/stories/" (:story-id @story)
                  "/tasks/" (:task-id @task))
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Edit task"]]
      [:div.panel-body
       [form-template]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:edit-task (:project-id @project) (assoc @task :story-id (:story-id @story))])}
         "Update"]]]]]))

(defn edit-story-button [project story]
  [:a.btn.btn-link {:href (str "/projects/" (:project-id @project)
                               "/stories/" (:story-id @story) "/edit")}
   [:i.glyphicon.glyphicon-edit]
   " Edit"])

(defn new-task-button [project story]
  [:a.btn.btn-link {:href (str "/projects/" (:project-id @project)
                               "/stories/" (:story-id @story)
                               "/tasks/new")}
   [:i.glyphicon.glyphicon-plus]
   " New task"])

(defn list-tasks [project tasks]
  [:ul.list-group
   (when-not (seq @tasks)
     [:li.list-group-item
      "No tasks yet."])
   (doall
     (for [task @tasks]
       ^{:key (:task-id task)}
       [:li.list-group-item
        {:class (when (= (:status task) 2) "list-group-item-success")}
        [:h3
         [:a {:href (str "/projects/" (:project-id @project)
                         "/stories/" (:story-id task)
                         "/tasks/" (:task-id task) "/edit")}
          (:title task)]
         [:div.pull-right
          (:priority-idx task)
          [:a.btn.btn-link {:href (str "/projects/" (:project-id @project)
                                       "/stories/" (:story-id task)
                                       "/tasks/" (:task-id task) "/edit")}
           [:i.glyphicon.glyphicon-edit]]
          [:button.btn.btn-link {:on-click #(rf/dispatch [:delete-task (:task-id task)])}
           [:i.glyphicon.glyphicon-remove]]]]
        [:p (:description task)]]))])

(defn project-tasks-page
  "Template listing all project's tasks"
  []
  (r/with-let [project (rf/subscribe [:project])
               tasks (rf/subscribe [:tasks])]
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:title "tasks"
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 (:title @project)
        [edit-project-button project]]]
      [list-tasks project tasks]]]))

(defn story-tasks-page
  "Template listing all the story's tasks"
  []
  (r/with-let [project (rf/subscribe [:project])
               story (rf/subscribe [:story])
               tasks (rf/subscribe [:tasks])]
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:href (str "/projects/" (:project-id @project) "/stories/" (:story-id @story))
       :title (:title @story)
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 (:title @story) " (" (count (<sub [:uncompleted-tasks])) ")"
        [edit-story-button project story]
        [:div.pull-right
         [new-task-button project story]]]]
      [list-tasks project tasks]]]))
