(ns manager.pages.stories
  (:require
   [manager.components :refer [base breadcrumbs form-group datetime-input-group]]
   [manager.pages.components :refer [edit-project-button]]
   [manager.utils :refer [done? full-name-or-email deadline-status]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [stand-lib.components :refer [pretty-display thead tbody]]
   [stand-lib.comps.forms :refer [input textarea select] :as forms]
   [stand-lib.re-frame.utils :refer [<sub]]))

(defn form-template-sidebar [story]
  (let [project (rf/subscribe [:projects/project])
        priorities (rf/subscribe [:priorities])
        types (rf/subscribe [:types])
        story-path (rf/subscribe [:stories/story-path])
        users (rf/subscribe [:users/all])
        add-owner? (rf/subscribe [:stories/add-owner?])]
    (fn []
      [:div
       (when-let [story-id (:story-id @story)]
         [:div.form-group
          [:label "story id"]
          [:input {:type :text
                   :class "form-control"
                   :value story-id
                   :disabled true}]])
       [:div.form-group
        [:label "project id"]
        [input {:type :text
                :class "form-control"
                :name (conj @story-path :project-id)
                :default-value (:project-id @project)
                :disabled true}]]
       [:div.form-group
        [:label "Status"]
        [:div
         (doall
           (for [{:keys [id name default?]} (<sub [:status])]
             ^{:key id}
             [:label.radio-inline
              [input {:type :radio
                      :name (conj @story-path :status)
                      :default-checked (and (not (:status @story)) default?)
                      :value name}]
              " " (clojure.string/capitalize name)]))]]
       [:div.form-group
        [:label "Type"]
        [:div.input-group
         [select {:class "form-control"
                  :name (conj @story-path :type)
                  :default-value (:idx (first @types))}
          (for [{:keys [name idx]} @types]
            ^{:key idx}
            [:option {:value idx}
             (clojure.string/capitalize name)])]
         [:div.input-group-addon "*"]]]
       [:div.form-group
        [:label "Priority"]
        [:div.input-group
         [select {:class "form-control"
                  :name (conj @story-path :priority-idx)
                  :default-value (:idx (first @priorities))}
          (for [{:keys [name idx]} @priorities]
            ^{:key idx}
            [:option {:value idx}
             (clojure.string/capitalize name)])]
         [:div.input-group-addon "*"]]]
       (if (or (:owner @story) @add-owner?)
         [:div.form-group
          [:label "Owner"]
          [:div.input-group
            [select {:class "form-control"
                     :name (conj @story-path :owner)
                     :default-value (:user-id (first @users))}
             (for [{:keys [user-id] :as user} @users]
               ^{:key user-id}
               [:option {:value user-id}
                (full-name-or-email user)])]
           [:div.input-group-addon
            {:on-click #(rf/dispatch [:stories/deassign-user (:owner @story) (:story-id @story)])}
            [:i.glyphicon.glyphicon-remove]]]]
         [:div.form-group
          [:label "Owner"] [:br]
          [:button.btn.btn-default {:on-click #(rf/dispatch [:stories/set-add-owner true])}
           "Nobody"]])
       [:div.form-group
        [:label "Deadline"]
        [datetime-input-group (conj @story-path :deadline)]]
       (when (:created-at @story)
         [:div.form-group
          [:label "Created at"]
          [input {:type :text
                  :class "form-control"
                  :name (conj @story-path :created-at)
                  :disabled true}]])
       (when (:updated-at @story)
         [:div.form-group
          [:label "Updated at"]
          [input {:type :text
                  :class "form-control"
                  :name (conj @story-path :updated-at)
                  :disabled true}]])])))

(defn form-template [story]
  (let [project (rf/subscribe [:projects/project])
        priorities (rf/subscribe [:priorities])
        types (rf/subscribe [:types])
        story-path (rf/subscribe [:stories/story-path])
        users (rf/subscribe [:users/all])
        add-owner? (rf/subscribe [:stories/add-owner?])]
    (fn []
      [:div.form-horizontal
       [form-group
        "Title"
        [:div.input-group
         [input {:type :text
                 :class "form-control"
                 :name (conj @story-path :title)
                 :auto-focus (when-not (:story-id @story) true)}]
         [:div.input-group-addon "*"]]]
       [form-group
        "Description"
        [textarea {:class "form-control"
                   :name (conj @story-path :description)
                   :rows 7
                   :default-value ""}]]])))

(defn task-items [tasks]
  (r/with-let [story-path (rf/subscribe [:stories/story-path])]
    (when (seq @tasks)
      [:table.table
       [:thead>tr
        [:th "Done?"]
        [:th "Title"]
        [:th "Original estimate"]
        [:th "Current estimate"]
        [:th "Delete"]]
       [:tbody
        (doall
          (for [task @tasks]
            (let [task-id (:task-id task)
                  input-class (if (:done? task) "form-control task-input-done" "form-control")]
              ^{:key (:task-id task)}
              [:tr {:class (when (:done? task) "task-done")}
               [:td [input {:type :checkbox
                            :name (conj @story-path :tasks task-id :done?)
                            :default-value (= (:status task) "done")}]]
               [:td [input {:type :text
                            :class input-class
                            :name (conj @story-path :tasks task-id :title)}]]
               [:td [input {:type :number
                            :class input-class
                            :name (conj @story-path :tasks task-id :orig-est)}]]
               [:td [input {:type :number
                            :class input-class
                            :name (conj @story-path :tasks task-id :curr-est)}]]
               [:td [:button.btn.btn-default
                     {:on-click #(rf/dispatch [:tasks/delete-task task-id])}
                     [:i.glyphicon.glyphicon-remove]]]])))]])))

(defn edit-story-page []
  (r/with-let [project (rf/subscribe [:projects/project])
               story (rf/subscribe [:stories/story])
               tasks (rf/subscribe [:stories.story/tasks])]
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:title (:title @story)
       :href (str "/projects/" (:project-id @project)
                  "/stories/" (:story-id @story))
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 (:title @story)]]
      [:div.panel-body
       [:div.col-md-9
        [form-template story]
        [:h4 "Tasks"]
        [task-items tasks]
        [:button.btn.btn-default
         {:on-click #(rf/dispatch [:stories/story-tasks-tick :story])}
         " Add task"]
        [:hr]
        [:div.row>div.col-sm-offset-2.col-sm-10
         [:button.btn.btn-primary
          {:on-click #(rf/dispatch [:stories/update-story-with-tasks @story])}
          "Update"]]]
       [:div.col-md-3
        [form-template-sidebar story]]]]]))


(defn new-story-page []
  (r/with-let [project (rf/subscribe [:projects/project])
               story (rf/subscribe [:stories/new-story])
               tasks (rf/subscribe [:stories.new-story/tasks])]
    [base
     [breadcrumbs
      {:href (str "/projects/" (:project-id @project))
       :title (:title @project)}
      {:title "New story", :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Create story"]]
      [:div.panel-body
       [:div.col-md-9
        [form-template story]
        [:h4 "Tasks"]
        [task-items tasks]
        [:button.btn.btn-default
         {:on-click #(rf/dispatch [:stories/story-tasks-tick :story])}
         " Add task"]
        [:hr]
        [:div.row>div.col-sm-offset-2.col-sm-10
         [:button.btn.btn-primary
          {:on-click #(rf/dispatch [:stories/create-story-with-tasks @story])}
          "Create"]]]
       [:div.col-md-3
        [form-template-sidebar story]]]]]))

(defn new-story-button [project]
  [:a.btn.btn-link
   {:href (str "/projects/" (:project-id @project) "/stories/new")}
   [:i.glyphicon.glyphicon-plus]
   " Create story"])

(defn list-stories [project stories]
  [:ul.list-group
   (when-not (seq @stories)
     [:li.list-group-item "No stories yet."])
   (doall
     (for [story @stories]
       ^{:key (:story-id story)}
       [:li.list-group-item
        {:class
         (cond
           (done? story) "archived"
           (= :expired (deadline-status (:deadline story))) "story-status story-expired"
           (= :warning (deadline-status (:deadline story))) "story-status story-warning"
           (= :on-schedule (deadline-status (:deadline story))) "story-status story-on-schedule"
           :else "story-status story-unscheduled")}
        [:div
         [:h3
          [:a {:href (str "/projects/" (:project-id @project) "/stories/" (:story-id story))}
           (:title story) " "
           (str "[" (:priority-idx story) "]")]
          [:div.pull-right
           [:button.btn.btn-link {:on-click #(rf/dispatch [:stories/delete-story (:project-id story) (:story-id story)])}
            [:i.glyphicon.glyphicon-remove]]]]
         ;; Display only the first line of the description.
         [:p (first
              (clojure.string/split-lines
               (:description story)))]]]))])

(defn project-stories-page []
  (r/with-let [project (rf/subscribe [:projects/project])
               pending-stories (rf/subscribe [:stories/pending])
               done-stories (rf/subscribe [:stories/done])
               show-completed-stories? (rf/subscribe [:stories/show-completed?])]
    [base
     [breadcrumbs
      {:title (:title @project)
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 (:title @project)
        [edit-project-button project]
        [:div.pull-right
         [new-story-button project]]]]
      [list-stories project pending-stories]
      [:button.btn.btn-default.btn-block
       {:on-click #(rf/dispatch [:stories/toggle-show-completed])}
       (if @show-completed-stories?
         "Hide completed stories"
         "Show completed stories")]
      (when @show-completed-stories?
        [list-stories project done-stories])]]))
