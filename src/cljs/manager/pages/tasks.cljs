(ns manager.pages.tasks
  (:require
   [manager.components :as c :refer [base breadcrumbs form-group tbody thead-editable]]
   [reagent.core :as r :refer [atom]]
   [reagent-forms.core :refer [bind-fields]]
   [re-frame.core :as rf]))

(defn tbody-editable
  "Conjs the edit and remove buttons to the row of items.
   Assumes the first item in the row is the id."
  []
  (r/with-let [project (rf/subscribe [:project])
               tasks (rf/subscribe [:tasks])]
    [tbody
     (map (fn [row]
            (conj row
                  [:a.btn.btn-default
                   {:href (str "/projects/" (:id @project) "/tasks/" (first row) "/edit")}
                   [:i.glyphicon.glyphicon-pencil]]
                  [:button.btn.btn-default
                   {:on-click #(rf/dispatch [:delete-task (:id @project) (first row)])}
                   [:i.glyphicon.glyphicon-remove]]))
          (map (juxt :id :feature :task :orig-est :curr-est :priority :elapsed :remain :status)
               @tasks))]))

(defn tasks [project]
  [:div
   [:table.table.table-striped.table-hover.table-bordered
    [thead-editable
      ["task-id"
       "Feature"
       "Task"
       "Orig est"
       "Curr est"
       "Priority"
       "Elapsed"
       "Remain"
       "Status"]]
    [tbody-editable]]])

(def form-template
  [:div.form-horizontal
   (form-group
     "Task id"
     [:input.form-control
      {:field :text, :id :id, :disabled true}])
   (form-group
     "Orig est"
     [:input.form-control
      {:field :numeric, :id :orig-est}])
   (form-group
     "Curr est"
     [:input.form-control
      {:field :numeric, :id :curr-est}])
   ;; TODO: fetch options from DB
   (form-group
     "Priority"
     [:select.form-control
      {:field :list, :id :priority}
      [:option {:key 1} "1 - urgent"]
      [:option {:key 2} "2 - high"]
      [:option {:key 3} "3 - important"]
      [:option {:key 4} "4 - medium"]
      [:option {:key 5} "5 - moderate"]
      [:option {:key 6} "6 - low"]
      [:option {:key 7} "7 - don’t fix"]])
   (form-group
     "Elapsed"
     [:input.form-control
      {:field :numeric, :id :elapsed}])
   (form-group
     "Remain"
     [:input.form-control
      {:field :numeric, :id :remain}])
   (form-group
     "Status"
     [:div.btn-group {:field :single-select :id :status}
      [:button.btn.btn-default {:key "-"} "-"]
      [:button.btn.btn-default {:key "on progress"} "on progress"]
      [:button.btn.btn-default {:key "done"} "done"]])])

(defn edit-task-page []
  (r/with-let [project (rf/subscribe [:project])
               feature (rf/subscribe [:feature])
               task (rf/subscribe [:task])
               doc (atom {})]
    (reset! doc (assoc @task :feature-id (:feature-id @feature)))

    [base
     (if @task
       [breadcrumbs
        {:href (str "/projects/" (:id @project))
         :title (:title @project)}
        {:href (str "/projects/" (:id @project) "/features/" (:feature-id @feature))
         :title (:title @feature)}
        {:title (:task @task)
         :href (str "/projects/" (:id @project)
                    "/features/" (:feature-id @feature)
                    (:task-id @task))}
        {:title "Edit", :active? true}]
       [breadcrumbs
        {:href (str "/projects/" (:id @project))
         :title (:title @project)}
        {:href (str "/projects/" (:id @project) "/features/" (:feature-id @feature))
         :title (:title @feature)}
        {:title "New task", :active? true}])
     [:div.panel.panel-default
      [:div.panel-heading
       (if @task
         [:h2 "Edit task"]
         [:h2 "Create task"])]
      [:div.panel-body
       [bind-fields
        form-template
        doc]
       [:div.col-sm-offset-2.col-sm-10
        (if @task
          [:button.btn.btn-primary
           {:on-click #(rf/dispatch [:edit-task @doc])}
           "Save"]
          [:button.btn.btn-primary
           {:on-click #(rf/dispatch [:create-task @doc])}
           "Create"])]]]]))

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
         [:div.col-md-2 "id"]
         [:div.col-md-10
          (:task-id @task)]]]
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 "Description"]
         [:div.col-md-10
          (:description @task)]]]
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 "Orig est"]
         [:div.col-md-10
          (:orig-est @task)]]]
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 "Curr est"]
         [:div.col-md-10
          (:curr-est @task)]]]
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 "Priority"]
         [:div.col-md-10
          (:priority @task)]]]
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 "Elapsed"]
         [:div.col-md-10
          (:elapsed @task)]]]
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 "Remain"]
         [:div.col-md-10
          (:remain @task)]]]
       [:li.list-group-item
        [:div.row
         [:div.col-md-2 "Status"]
         [:div.col-md-10
          (:status @task)]]]]]]))

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
