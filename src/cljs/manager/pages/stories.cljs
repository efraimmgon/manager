(ns manager.pages.stories
  (:require
   [manager.components :refer [base breadcrumbs form-group]]
   [manager.pages.components :refer [edit-project-button]]
   [manager.utils :refer [done?]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [stand-lib.components :refer [pretty-display thead tbody]]
   [stand-lib.comps.forms :refer [input textarea select]]
   [stand-lib.local-store :as ls]
   [stand-lib.re-frame.utils :refer [<sub]]))

(defn checkbox-single-input
  [attrs]
  (let [field-value (rf/subscribe [:query (:name attrs)])
        f (fn [acc] (if (nil? acc) (:value attrs) nil))
        edited-attrs
        (-> attrs
            (assoc :type :checkbox)
            (update :on-change #(or % (fn [e] (rf/dispatch [:update-state (:name attrs) f]))))
            (assoc :checked (or (:default-checked attrs)
                                (= (:value attrs) @field-value)))
            (dissoc :default-checked))]
    ;; persist value when it's the default
    (when (and (:checked edited-attrs)
               (not (= @field-value (:value attrs))))
      (rf/dispatch [:update-state (:name attrs) f]))
    [:input edited-attrs]))

(defn form-template [story]
  (r/with-let [project (rf/subscribe [:projects/project])
               priorities (rf/subscribe [:priorities])
               types (rf/subscribe [:types])
               story-path (rf/subscribe [:stories/story-path])]
    [:div.form-horizontal
     (when-not (:project-id @story)
       (rf/dispatch [:set-state (conj @story-path :project-id) (:project-id @project)])
       (rf/dispatch [:set-state (conj @story-path :description) ""]))
     (when-let [story-id (:story-id @story)]
       [form-group
        "story id"
        [:input {:type :text
                 :class "form-control"
                 :value story-id
                 :disabled true}]])
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
                 :rows 7}]]
     [form-group
      "Status"
      [:div
       (doall
         (for [{:keys [id name default?]} (<sub [:status])]
           ^{:key id}
           [:label.radio-inline
            [input {:type :radio
                    :name (conj @story-path :status)
                    :default-checked (and (nil? (:status @story)) default?)
                    :value name}]
            " " (clojure.string/capitalize name)]))]]
     [form-group
      "Type"
      [:div.input-group
       [select {:class "form-control"
                :name (conj @story-path :type)
                :default-value (:idx (first @types))}
        (for [{:keys [name idx]} @types]
          ^{:key idx}
          [:option {:value idx}
           (clojure.string/capitalize name)])]
       [:div.input-group-addon "*"]]]
     [form-group
      "Priority"
      [:div.input-group
       [select {:class "form-control"
                :name (conj @story-path :priority-idx)
                :default-value (:idx (first @priorities))}
        (for [{:keys [name idx]} @priorities]
          ^{:key idx}
          [:option {:value idx}
           (clojure.string/capitalize name)])]
       [:div.input-group-addon "*"]]]
     (when (:created-at @story)
       [form-group
        "Created at"
        [input {:type :text
                :class "form-control"
                :name (conj @story-path :created-at)
                :disabled true}]])
     (when (:updated-at @story)
       [form-group
        "Updated at"
        [input {:type :text
                :class "form-control"
                :name (conj @story-path :updated-at)
                :disabled true}]])]))

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
                  input-class (if (done? task) "form-control task-input-done" "form-control")]
              ^{:key (:task-id task)}
              [:tr {:class (when (done? task) "task-done")}
               [:td [checkbox-single-input
                     {:name (conj @story-path :tasks task-id :status)
                      :value "done"}]]
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

; Load story with tasks
; When clicked on "update" tasks with no story-id are saved, while tasks
; with an id are updated
; Optionally I can use the on-blur event to persist changes
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
       [:h2 "Edit story"]]
      [:div.panel-body
       [form-template story]
       [:h3 "Tasks"]
       [task-items tasks]
       [:button.btn.btn-default
        ; TODO
        {:on-click #(rf/dispatch [:stories/story-tasks-tick :story])}
        [:i.glyphicon.glyphicon-plus]
        " Add task"]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:stories/update-story-with-tasks @story])}
         "Update"]]]]]))

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
       [form-template story]
       [:h3 "Tasks"]
       [task-items tasks]
       [:button.btn.btn-default
        {:on-click #(rf/dispatch [:stories/story-tasks-tick :new-story])}
        [:i.glyphicon.glyphicon-plus]
        " Add task"]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:stories/create-story-with-tasks @story])}
         "Create"]]]]]))

(defn new-story-button [project]
  [:a.btn.btn-link
   {:href (str "/projects/" (:project-id @project) "/stories/new")
    :on-click #(rf/dispatch [:stories/set-story-path [:stories :new-story]])}
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
        {:class (when (done? story) "archived")}
        [:h3
         [:a {:href (str "/projects/" (:project-id @project) "/stories/" (:story-id story))
              :on-click #(rf/dispatch [:stories/set-story-path [:stories :story]])}
          (:title story) " "
          (str "[" (:priority-idx story) "]")]
         [:div.pull-right
          [:button.btn.btn-link {:on-click #(rf/dispatch [:stories/delete-story (:project-id story) (:story-id story)])}
           [:i.glyphicon.glyphicon-remove]]]]
        [:p (first
             (clojure.string/split-lines
              (:description story)))]]))])

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
