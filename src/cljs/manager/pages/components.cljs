(ns manager.pages.components)

(defn edit-project-button [project]
  [:a.btn.btn-link {:href (str "/projects/" (:project-id @project) "/edit")}
   [:i.glyphicon.glyphicon-edit]
   " Edit"])
