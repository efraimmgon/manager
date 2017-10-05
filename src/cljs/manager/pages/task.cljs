(ns manager.pages.task
  (:require
   [manager.components :as c :refer [base breadcrumbs tbody thead-editable]]
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


(defn form-group [label & input]
  [:div.form-group
   [:label.col-sm-2.control-label label]
   (into
    [:div.col-sm-10]
    input)])

(def form-template
  [:div.form-horizontal
   (form-group
     "Task id"
     [:input.form-control
      {:field :text, :id :id, :disabled true}])
   (form-group
     "Feature"
     [:input.form-control
      {:field :text, :id :feature}])
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
               task (rf/subscribe [:task])
               doc (atom @task)]
    [base
     [breadcrumbs
      [(str "/projects/" (:id @project)) (:title @project)]
      [nil (:task @task) :active]]

     [:div.panel.panel-default
      [:div.panel-heading
       [:h2
        "Edit: "
        [:a {:href (str "/projects/" (:id @project))}
         (:title @project)]
        " / "
        (:task @task)]]
      [:div.panel-body
       [c/pretty-display "task" task]
       [c/pretty-display "doc" doc]

       [bind-fields
        form-template
        doc]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-default
         {:on-click #(rf/dispatch [:edit-task (:id @project) @doc])}
         "Save"]]]]]))
