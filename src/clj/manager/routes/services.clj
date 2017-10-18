(ns manager.routes.services
  (:require
   [ring.util.http-response :refer :all]
   [compojure.api.sweet :refer :all]
   [schema.core :as s]
   [compojure.api.meta :refer [restructure-param]]
   [buddy.auth.accessrules :refer [restrict]]
   [buddy.auth :refer [authenticated?]]
   [manager.routes.services.projects :as projects]
   [manager.routes.services.features :as features]))

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

    ;;; PROJECTS

    (GET "/projects" []
         :summary "list available projects"
         (projects/get-all-projects))

    (GET "/projects/:id" []
         :path-params [id :- s/Int]
         :summary "get project by id"
         (projects/get-project {:project-id id}))

    (POST "/projects" req
          :summary "create a new project"
          (projects/create-project! (:params req)))

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

    (DELETE "/projects" []
            :body-params [project-id :- s/Int]
            :return s/Int
            :summary "delete project by id"
            (projects/delete-project! {:project-id project-id}))

    ;;; FEATURES

    (GET "/features/by-project/:project-id" []
         :path-params [project-id :- s/Int]
         :summary "get features by project-id"
         (features/get-features-by-project {:project-id project-id}))))
