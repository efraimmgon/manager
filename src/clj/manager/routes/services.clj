(ns manager.routes.services
  (:require
   [ring.util.http-response :refer :all]
   [compojure.api.sweet :refer :all]
   [schema.core :as s]
   [compojure.api.meta :refer [restructure-param]]
   [buddy.auth.accessrules :refer [restrict]]
   [buddy.auth :refer [authenticated?]]
   [manager.routes.services.projects :as projects]))

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

    (GET "/projects" []
         :summary "list available projects"
         (projects/all-projects))

    (POST "/projects" req
          :summary "create a new project"
          (projects/create-project! (:params req)))))
