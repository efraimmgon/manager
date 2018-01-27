(ns manager.db
  (:require [cljs.spec.alpha :as s]))

; ------------------------------------------------------------------------------
; Spec
; ------------------------------------------------------------------------------

;;; Common fields
(s/def ::name string?)
(s/def ::idx int?)
(s/def ::title string?)
(s/def ::description string?)
(s/def ::priority-idx (s/int-in 1 8))
(s/def ::status #{"done" "pending"})
(s/def ::type (s/int-in 1 4))
(s/def ::created-at string?) ; ISO String
(s/def ::updated-at ::created-at)

;;; Status
(s/def :status/id #{:pending, :done})
(s/def :status/status (s/keys :req-un [:status/id ::name ::idx]))
(s/def ::status (s/* :status/status))

;;; Priority
(s/def :priority/id #{:urgent, :high, :important, :medium, :moderate, :low, :dont-fix})
(s/def :priorities/priority (s/keys :req-un [:priority/id ::name ::idx]))
(s/def ::priorities (s/* :priorities/priority))

;;; Types
(s/def :types/type (s/keys :req-un [::name ::idx]))
(s/def ::types (s/* :types/type))

;;; Tasks
(s/def :task/task-id int?)
(s/def :task/orig-est (s/and number? pos?))
(s/def :task/curr-est :task/orig-est)
(s/def :task/velocity (s/and #(< 0 %) #(<= % 1)))
(s/def :tasks/task
       (s/keys :req-un [:task/task-id
                        :story/story-id
                        ::title
                        :task/orig-est
                        :task/curr-est
                        ::status
                        :task/velocity
                        ::created-at
                        ::updated-at]))
(s/def :tasks/tasks (s/* :tasks/task))

;;; Stories ns
(s/def :stories/show-completed? boolean?)
(s/def :stories/story
       (s/keys :req-un [:story/story-id
                        :project/project-id
                        ::title
                        ::description
                        ::priority-idx
                        ::status
                        ::type
                        ::created-at
                        ::updated-at]))
(s/def :stories/new-story
       (s/keys :req-un [:project/project-id
                        ::title
                        ::description
                        ::priority-idx
                        ::status
                        ::type]))
(s/def :stories/stories (s/* :stories/story))
(s/def ::stories (s/keys :req-un [:stories/show-completed?]
                         :opt-un [:stories/stories]))

;;; Project ns
(s/def :project/project-id int?)
(s/def :projects/project
       (s/keys :req-un [:project/project-id
                        ::title
                        ::description
                        ::created-at
                        ::updated-at]))
(s/def :projects/new-project
       (s/keys :req-un [::title
                        ::description]))
(s/def :projects/all (s/* :projects/project))

(s/def ::projects
       (s/keys :opt-un [:projects/all
                        :projects/project
                        :projects/new-project]))

(s/def ::page keyword?)

(s/def ::db (s/keys :req-un [::page ::status ::priorities ::types ::stories]
                    :opt-un [::projects]))

; ------------------------------------------------------------------------------
; app-db
; ------------------------------------------------------------------------------

(def default-db
  {:page :home
   :status [{:id :done :name "done" :idx 1},
            {:id :pending :name "pending" :default? true :idx 2}]
   :priorities [{:id :urgent, :name "urgent" :idx 1},
                {:id :high, :name "high" :idx 2},
                {:id :important, :name "Important" :idx 3},
                {:id :medium, :name "Medium" :idx 4},
                {:id :moderate, :name "Moderate" :idx 5},
                {:id :low, :name "Low" :idx 6},
                {:id :dont-fix, :name "Don't fix" :idx 7}]
   :types [{:name "feature", :idx 1}
           {:name "bug", :idx 2}
           {:name "chore", :idx 3}]
   :stories {:show-completed? false}})
