(ns manager.routes.services
  (:require
   [clojure.spec.alpha :as s]
   [spec-tools.core :as st]
   [spec-tools.spec :as spec]
   [ring.util.http-response :refer :all]
   [compojure.api.sweet :refer :all]
   [schema.core :as schema]
   [compojure.api.meta :refer [restructure-param]]
   [buddy.auth.accessrules :refer [restrict]]
   [buddy.auth :refer [authenticated?]]
   [manager.routes.services.domain :as domain]
   [manager.routes.services.projects :as projects]
   [manager.routes.services.stories :as stories]
   [manager.routes.services.tasks :as tasks]
   [manager.routes.services.users :as users]))

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

(def service-routes
  (api
    {:swagger {:ui "/swagger-ui"
               :spec "/swagger.json"
               :data {:info {:version "1.0.0"
                             :title "Sample API"
                             :description "Sample Services"}}}}

    (context
      "/api" []
      :tags ["private"]
      :coercion :spec

      ;;; PROJECTS

      ; ; LIST
      (GET "/projects" []
           :return :projects/projects
           :summary "list available projects"
           (projects/get-all-projects))

      ; CREATE
      (POST "/projects" []
            :body-params [title       :- ::domain/title
                          description :- ::domain/description]
            :return (s/keys :req-un [:project/project-id])
            :summary "create a new project"
            (projects/create-project!
             {:title title
              :description description}))

      ; READ
      (GET "/projects/:id" []
           :path-params [id :- :project/project-id]
           :return :projects/project
           :summary "get project by id"
           (projects/get-project {:project-id id}))

      ; UPDATE
      (PUT "/projects" []
           :body-params [project-id  :- :project/project-id
                         title       :- ::domain/title
                         description :- ::domain/description]
           :return int?
           :summary "update project given project-id; returns the number of affected rows"
           (projects/update-project!
            {:project-id project-id
             :title title
             :description description}))

      ; DELETE
      (DELETE "/projects" []
              :body-params [project-id :- :project/project-id]
              :return int?
              :summary "delete project by id; returns the number of affected rows"
              (projects/delete-project! {:project-id project-id}))


      ;;; stories

      ; LIST
      (GET "/projects/:project-id/stories" []
           :path-params [project-id :- :project/project-id]
           :return :stories/stories
           :summary "get stories by project-id"
           (stories/get-stories-by-project {:project-id project-id}))

      ; CREATE
      (POST "/projects/:project-id/stories" []
            :path-params [project-id  :- :project/project-id]
            :body-params [project-id  :- :project/project-id
                          title       :- ::domain/title
                          description :- ::domain/description
                          type        :- ::domain/type
                          priority-idx :- ::domain/priority-idx
                          status      :- ::domain/status
                          tasks       :- :tasks.new-without-story/tasks]
            :return (s/keys :req-un [:story/story-id])
            :summary "create a new story for project-id"
            (stories/create-story-with-tasks!
             {:project-id project-id
              :title title
              :description description
              :type type
              :priority-idx priority-idx
              :status status
              :tasks tasks}))

      ; READ
      (GET "/stories/:story-id" []
           :path-params [story-id :- :story/story-id]
           :return :stories/story
           :summary "get story by story-id"
           (stories/get-story {:story-id story-id}))

      ; READ + tasks
      (GET "/stories/:story-id/with-tasks" []
           :path-params [story-id :- :story/story-id]
           :return :stories/story
           :summary "get story by story-id with tasks"
           (stories/get-story-with-tasks {:story-id story-id}))


      ; UPDATE
      (PUT "/stories" []
           :body-params [story-id :- :story/story-id
                         title :- ::domain/title
                         description :- ::domain/description]
           :return int?
           :summary "update story by story-id; returns num of affected rows"
           (stories/update-story!
            {:story-id story-id
             :title title
             :description description}))

      ; UPDATE + tasks
      (PUT "/stories/:story-id/with-tasks" req
           :body-params [story-id    :- :story/story-id
                         project-id  :- :project/project-id
                         title       :- ::domain/title
                         description :- ::domain/description
                         type        :- ::domain/type
                         priority-idx :- ::domain/priority-idx
                         status      :- ::domain/status
                         tasks       :- :maybe-new/tasks]
           :return int?
           :summary "update story by story-id; returns num of affected rows"
           (stories/update-story-with-tasks!
            ; (:params req)))
            {:story-id story-id
             :title title
             :description description
             :type type
             :priority-idx priority-idx
             :status status
             :tasks tasks}))

      ; DELETE
      (DELETE "/stories" []
              :body-params [story-id :- :story/story-id]
              :return int?
              :summary "delete story by story-id; returns num of affected rows"
              (stories/delete-story!
               {:story-id story-id}))

      ;;; TASKS

      ; CREATE

      (POST "/stories/:story-id/tasks" []
            :body-params [story-id    :- :story/story-id
                          title       :- ::domain/title
                          description :- ::domain/description
                          orig-est    :- :task/orig-est
                          curr-est    :- :task/curr-est
                          status      :- ::domain/status]
            :return :task/task-id
            :summary "create a task for story-id; returns the task-id map"
            (tasks/create-task!
             {:story-id story-id
              :title title
              :description description
              :orig-est orig-est
              :curr-est curr-est
              :status status}))

      ; LIST
      (GET "/stories/:story-id/tasks" []
           :path-params [story-id :- :story/story-id]
           :return :tasks/tasks
           :summary "get tasks by story-id"
           (tasks/get-tasks
            {:story-id story-id}))

      ; READ
      (GET "/tasks/:task-id" []
           :path-params [task-id :- :task/task-id]
           :return :tasks/task
           :summary "get task by task-id"
           (tasks/get-task {:task-id task-id}))

      ; UPDATE
      (PUT "/tasks" []
           :body-params [task-id     :- :task/task-id
                         story-id    :- :story/story-id
                         title       :- ::domain/title
                         description :- ::domain/description
                         orig-est    :- :task/orig-est
                         curr-est    :- :task/curr-est
                         status      :- ::domain/status]
           :return int?
           :summary "update task by task-id; returns the num of affected rows"
           (tasks/update-task!
            {:task-id task-id
             :story-id story-id
             :title title
             :description description
             :orig-est orig-est
             :curr-est curr-est
             :status status}))

      ; DELETE
      (DELETE "/tasks" []
              :body-params [task-id :- :task/task-id]
              :return int?
              :summary "delete task by task-id; returns the num of affected rows"
              (tasks/delete-task! {:task-id task-id}))

      (context
       "/users" []

       (GET "/" []
            :return :users/users
            :summary "get users"
            (users/get-users))
       (POST "/" []
             :body [user :new/user]
             :return (s/keys :req-un [:users/user-id])
             :summary "create a user"
             (users/create-user<! user))
       (GET "/:user-id" []
            :path-params [user-id :- :users/user-id]
            :return :users/user
            :summary "get user by id"
            (users/get-user {:user-id user-id}))
       (PUT "/:user-id" []
            :path-params [user-id     :- :users/user-id]
            :body-params [first-name  :- :users/first-name
                          last-name   :- :users/last-name
                          email       :- :users/email
                          admin       :- :users/admin
                          last-login  :- :users/last-login
                          is-active   :- :users/is-active
                          pass        :- :users/pass]
            :return int?
            :summary "update an user by id; return the num of affected rows."
            (users/update-user! {:user-id user-id
                                 :first-name first-name
                                 :last-name last-name
                                 :email email
                                 :admin admin
                                 :last-login last-login
                                 :is-active is-active
                                 :pass pass}))
       (DELETE "/:user-id" []
               :path-params [user-id :- :users/user-id]
               :return int?
               :summary "delete user by id; return the num of affected rows."
               (users/delete-user! {:user-id user-id}))))))
