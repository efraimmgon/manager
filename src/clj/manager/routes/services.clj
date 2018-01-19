(ns manager.routes.services
  (:require
   [ring.util.http-response :refer :all]
   [compojure.api.sweet :refer :all]
   [schema.core :as s]
   [compojure.api.meta :refer [restructure-param]]
   [buddy.auth.accessrules :refer [restrict]]
   [buddy.auth :refer [authenticated?]]
   [manager.routes.services.priorities :as priorities]
   [manager.routes.services.projects :as projects]
   [manager.routes.services.stories :as stories]
   [manager.routes.services.status :as status]
   [manager.routes.services.tasks :as tasks]))


(defn access-error [_ _]
  (unauthorized {:error "unauthorized"}))

(defn wrap-restricted [handler rule]
  (restrict handler {:handler  rule
                     :on-error access-error}))

(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [wrap-restricted rule]))

(defmethod restructure-param :current-user
  [_ binding acc]
  (update-in acc [:letks] into [binding `(:identity ~'+compojure-api-request+)]))

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Sample API"
                           :description "Sample Services"}}}}

  (context
    "/api" []
    :tags ["private"]
    ;:coercion :schema
    ;;; PROJECTS

    ; LIST
    (GET "/projects" []
         :return [projects/Project]
         :summary "list available projects"
         (projects/get-all-projects))

    ; CREATE
    (POST "/projects" []
          :body-params [title       :- s/Str
                        description :- s/Str]
          :return [{:project-id s/Int}]
          :summary "create a new project"
          (projects/create-project!
           {:title title
            :description description}))

    ; READ
    (GET "/projects/:id" []
         :path-params [id :- s/Int]
         :return projects/Project
         :summary "get project by id"
         (projects/get-project {:project-id id}))

    (GET "/projects/:id/tasks/unfineshed" []
         :path-params [id :- s/Int]
         :return [tasks/Task]
         :summary "get tasks by project-id"
         (tasks/get-unfineshed-tasks-by-project {:project-id id}))

    (GET "/projects/:id/tasks/recently-updated" []
         :path-params [id :- s/Int]
         :return [tasks/Task]
         :summary "get tasks by project-id ordered by date updated"
         (tasks/get-recently-updated-tasks-by-project {:project-id id}))

    ; UPDATE
    (PUT "/projects" []
         :body-params [project-id  :- s/Int
                       title       :- s/Str
                       description :- s/Str]
         :return s/Int
         :summary "update project given project-id"
         (projects/update-project!
          {:project-id project-id
           :title title
           :description description}))

    ; DELETE
    (DELETE "/projects" []
            :body-params [project-id :- s/Int]
            :return s/Int
            :summary "delete project by id"
            (projects/delete-project! {:project-id project-id}))


    ;;; stories

    ; LIST
    (GET "/projects/:project-id/stories" []
         :path-params [project-id :- s/Int]
         :return [stories/story]
         :summary "get stories by project-id"
         (stories/get-stories-by-project {:project-id project-id}))

    ; CREATE
    (POST "/projects/:project-id/stories" []
          :path-params [project-id  :- s/Int]
          :body-params [title       :- s/Str
                        description :- s/Str]
          :return [{:story-id s/Int}]
          :summary "create a new story for project-id"
          (stories/create-story!
           {:project-id project-id
            :title title
            :description description}))

    ; READ
    (GET "/stories/:story-id" []
         :path-params [story-id :- s/Int]
         :return stories/story
         :summary "get stories by project-id"
         (stories/get-story {:story-id story-id}))

    ; UPDATE
    (PUT "/stories" []
         :body-params [story-id :- s/Int
                       title :- s/Str
                       description :- s/Str]
         :return s/Int
         :summary "update story by story-id"
         (stories/update-story!
          {:story-id story-id
           :title title
           :description description}))

    ; DELETE
    (DELETE "/stories" []
            :body-params [story-id :- s/Int]
            :return s/Int
            :summary "delete story by story-id"
            (stories/delete-story!
             {:story-id story-id}))

    ;;; TASKS

    ; CREATE

    (POST "/stories/:story-id/tasks" []
          :body-params [story-id  :- s/Int
                        title       :- s/Str
                        description :- s/Str
                        orig-est    :- s/Int
                        curr-est    :- s/Int
                        elapsed     :- s/Int
                        remain      :- s/Int
                        priority-idx :- s/Int
                        status   :- s/Int]
          :return [{:task-id s/Int}]
          :summary "create a task for story-id"
          (tasks/create-task!
           {:story-id story-id
            :title title
            :description description
            :orig-est orig-est
            :curr-est curr-est
            :elapsed elapsed
            :remain remain
            :priority-idx priority-idx
            :status status}))

    ; LIST
    (GET "/stories/:story-id/tasks" []
         :path-params [story-id :- s/Int]
         :return [tasks/Task]
         :summary "get tasks by story-id"
         (tasks/get-tasks
          {:story-id story-id}))

    ; READ
    (GET "/tasks/:task-id" []
         :path-params [task-id :- s/Int]
         :return tasks/Task
         :summary "get task by task-id"
         (tasks/get-task {:task-id task-id}))

    ; UPDATE
    (PUT "/tasks" []
         :body-params [task-id     :- s/Int
                       story-id  :- s/Int
                       title       :- s/Str
                       description :- s/Str
                       orig-est    :- s/Int
                       curr-est    :- s/Int
                       elapsed     :- s/Int
                       remain      :- s/Int
                       priority-idx :- s/Int
                       status   :- s/Int]
         :return s/Int
         :summary "update task by task-id"
         (tasks/update-task!
          {:task-id task-id
           :story-id story-id
           :title title
           :description description
           :orig-est orig-est
           :curr-est curr-est
           :elapsed elapsed
           :remain remain
           :priority-idx priority-idx
           :status status}))

    ; DELETE
    (DELETE "/tasks" []
            :body-params [task-id :- s/Int]
            :return s/Int
            :summary "delete task by task-id"
            (tasks/delete-task! {:task-id task-id}))

    ;;; Status

    (GET "/status" []
         :return [status/Status]
         :summary "get all available status"
         (status/get-all-status))

    ;;; Priorities

    (GET "/priorities" []
         :return [priorities/Priority]
         :summary "get all available priorities"
         (priorities/get-priorities))))
