(ns manager.pages.components)   

(defn edit-project-button [project]
  [:a.btn.btn-link {:href (str "/projects/" (:project-id @project) "/edit")}
   [:i.glyphicon.glyphicon-edit]
   " Edit"])

(defn list-unfinished-tasks-button [project]
  [:a.btn.btn-link {:href (str "/projects/" (:project-id @project) "/tasks/unfineshed")}
   [:i.glyphicon.glyphicon-th-list]
   " List pending tasks"])
