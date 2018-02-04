(ns manager.pages.users
  (:require
   [manager.components :refer [base breadcrumbs]]
   [reagent.core :as r]
   [re-frame.core :as rf]))

; link to users
; load users
; render page

(defn list-users-page []
  (r/with-let [users (rf/subscribe [:users/all])]
    [base
     [breadcrumbs]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Users"
        [:div.pull-right
         [:a.btn.btn-link {:href (str "/users/new")}
          [:i.glyphicon.glyphicon-plus]
          " New user"]]]]
      [:ul.list-group
       (when-not (seq @users)
         [:li.list-group-item "No users yet."])
       (for [user @users]
         ^{:key (:user-id user)}
         [:li.list-group-item
          [:h3
           ;; TODO read
           [:a {:href (str "/users/" (:user-id user))}
            (:title user)]
           [:div.pull-right
            ;; TODO edit
            [:a.btn.btn-link
             {:href (str "/users/" (:user-id user) "/edit")}
             [:i.glyphicon.glyphicon-edit]]
            ;; TODO delete
            [:button.btn.btn-link {:on-click #(rf/dispatch [:users/delete-user (:user-id user)])}
             [:i.glyphicon.glyphicon-remove]]]]])]]]))
