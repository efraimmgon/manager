(ns manager.db
  (:require [cljs.spec.alpha :as s]))

; ------------------------------------------------------------------------------
; Spec
; ------------------------------------------------------------------------------

;;; Common fields
(s/def :common/name string?)
(s/def :common/idx int?)
(s/def :common/title string?)
(s/def :common/description string?)
(s/def :common/priority-idx number?)
(s/def :common/status int?)
(s/def :common/type int?)
(s/def :common/created-at string?) ; ISO String
(s/def :common/updated-at :common/created-at)

;;; Status
(s/def :status/id #{:pending, :done})
(s/def :status/status (s/keys :req-un [:status/id :common/name :common/idx]))
(s/def ::status (s/* :status/status))

;;; Priority
(s/def :priority/id #{:urgent, :high, :important, :medium, :moderate, :low, :dont-fix})
(s/def :priorities/priority (s/keys :req-un [:priority/id :common/name :common/idx]))
(s/def ::priorities (s/* :priorities/priority))

;;; Types
(s/def :types/type (s/keys :req-un [:common/name :common/idx]))
(s/def ::types (s/* :types/type))

;;; Tasks
(s/def :tasks.task/task-id int?)
(s/def :tasks.task/orig-est (s/and number? pos?))
(s/def :tasks.task/curr-est :tasks.task/orig-est)
(s/def :tasks.task/velocity (s/and #(< 0 %) #(<= % 1)))
(s/def :tasks/task
       (s/keys :req-un [:tasks.task/task-id
                        :stories.story/story-id
                        :common/title
                        :tasks.task/orig-est
                        :tasks.task/curr-est
                        :common/status
                        :tasks.task/velocity
                        :common/created-at
                        :common/updated-at]))
(s/def ::tasks (s/* :tasks/task))

;;; Stories ns
(s/def :stories/show-completed? boolean?)
(s/def :stories/story
       (s/keys :req-un [:stories.story/story-id
                        :projects.project/project-id,
                        :common/title
                        :common/description
                        :common/priority-idx
                        :common/status
                        :common/type
                        :common/created-at
                        :common/updated-at]))
(s/def :stories/stories (s/* :stories/story))
(s/def ::stories (s/keys :req-un [:stories/show-completed?]
                         :opt-un [:stories/stories]))

;;; Project ns
(s/def :projects.project/project-id int?)
(s/def :projects/project
       (s/keys :req-un [:projects.project/project-id,
                        :common/title,
                        :common/description
                        :common/created-at
                        :common/updated-at]))
(s/def :projects/all (s/* :projects/project))
(s/def ::projects (s/keys :opt-un [:project/all :projects/project]))

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
