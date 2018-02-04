(ns manager.routes.services.domain
  (:require
   [clojure.spec.alpha :as s]
   [spec-tools.core :as st]
   [spec-tools.spec :as spec])
  (:import
   [org.joda.time]))

; ------------------------------------------------------------------------------
; Common
; ------------------------------------------------------------------------------

(s/def ::id spec/int?)
(s/def ::title spec/string?)
(s/def ::description spec/string?)
(s/def ::created-at (st/spec (s/or :inst #(instance? org.joda.time.DateTime %)
                                   :string string?)))
(s/def ::updated-at ::created-at)
(s/def ::priority-idx (s/int-in 1 8))

(s/def ::type (s/and spec/int? (s/int-in 1 5)))
(s/def ::status (st/spec #{"pending", "done"}))

; ------------------------------------------------------------------------------
; Projects
; ------------------------------------------------------------------------------

(s/def :project/project-id ::id)

(s/def :projects/project
       (s/keys :req-un [:project/project-id
                        ::title
                        ::description
                        ::created-at
                        ::updated-at]))
(s/def :projects/projects (s/* :projects/project))

; ------------------------------------------------------------------------------
; Stories
; ------------------------------------------------------------------------------

(s/def :story/story-id ::id)
(s/def :stories/story
       (s/keys :req-un [:story/story-id
                        :project/project-id,
                        ::title
                        ::description
                        ::priority-idx
                        ::status
                        ::type
                        ::created-at
                        ::updated-at]
               :opt-un [:tasks/tasks]))
(s/def :stories/stories (s/* :stories/story))

(s/def :stories.new/story
       (s/keys :req-un [::title
                        ::description
                        ::type
                        ::priority-idx
                        ::status]
               :opt-un [:tasks.new-without-story/tasks]))


; ------------------------------------------------------------------------------
; Tasks
; ------------------------------------------------------------------------------

(s/def :task/task-id ::id)
(s/def :task/orig-est (s/and number? pos?))
(s/def :task/curr-est :task/orig-est)
(s/def :task/velocity (s/nilable (s/and #(< 0 %) #(<= % 1))))
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

(s/def :maybe-new/task
       (st/spec
         (s/keys :req-un [::title
                          :task/orig-est
                          :task/curr-est
                          ::status]
                 :opt-un [:task/task-id
                          :story/story-id
                          ::created-at
                          ::updated-at
                          :task/velocity])))

(s/def :maybe-new/tasks (s/* :maybe-new/task))

(s/def :tasks.new/task
       (s/keys :req-un [:story/story-id
                        ::title
                        :task/orig-est
                        :task/curr-est
                        ::status]))

(s/def :tasks.new-without-story/task
       (s/keys :req-un [::title
                        :task/orig-est
                        :task/curr-est
                        ::status]))
(s/def :tasks.new-without-story/tasks
       (s/* :tasks.new-without-story/task))

; ------------------------------------------------------------------------------
; Users
; ------------------------------------------------------------------------------

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")

(s/def :users/first-name string?)
(s/def :users/last-name string?)
(s/def :users/email (s/and string? #(re-matches email-regex %)))
(s/def :users/admin boolean?)
(s/def :users/last-login ::created-at)
(s/def :users/is-active boolean?)
(s/def :users/pass string?)

(s/def :users/user
       (s/keys :req-un [:users/first-name
                        :users/last-name
                        :users/email
                        :users/admin
                        :users/last-login
                        :users/is-active
                        ::created-at
                        ::updated-at
                        :users/pass]))

(s/def :new/user
       (s/keys :req-un [:users/email
                        :users/admin
                        :users/pass]
               :opt-un [:users/first-name
                        :users/last-name]))

(s/def :users/users (s/* :users/user))
