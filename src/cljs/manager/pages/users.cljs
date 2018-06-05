(ns manager.pages.users
  (:require
   [manager.components :refer [base breadcrumbs form-group]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [stand-lib.comps.forms :refer [input textarea select]]
   [stand-lib.components :refer [pretty-display tabulate]]))

(defn form-template []
  (let [user (rf/subscribe [:users/user])
        ns [:users :user]]
    (when-not (:user-id @user)
      (rf/dispatch [:set-state (conj ns :is-active) true]))
    (fn []
      [:div.form-horizontal
       (when (:user-id @user)
         [form-group
          "User id"
          [:input {:class "form-control"
                   :type :text
                   :value (:user-id @user)
                   :disabled true}]])
       [form-group
        "First name"
        [input {:type :text
                :class "form-control"
                :name (conj ns :first-name)
                :auto-focus true}]]
       [form-group
        "Last name"
        [input {:type :text
                :class "form-control"
                :name (conj ns :last-name)}]]
       [form-group
        "Email"
        [input {:type :email
                :class "form-control"
                :name (conj ns :email)}]]
       [form-group
        "Password"
        [input {:type :password
                :class "form-control"
                :name (conj ns :pass)}]]
       [form-group
        "Active?"
        [input {:type :checkbox
                :class "form-control"
                :name (conj ns :is-active)}]]
       [form-group
        "Admin?"
        [input {:type :checkbox
                :class "form-control"
                :name (conj ns :admin)}]]
       (when (:created-at @user)
         [form-group
          "Created at"
          [:input {:class "form-control"
                   :type :text
                   :value (:created-at @user)
                   :disabled true}]])
       (when (:updated-at @user)
         [form-group
          "Updated at"
          [:input {:class "form-control"
                   :type :text
                   :value (:updated-at @user)
                   :disabled true}]])])))

(defn new-user-page []
  (r/with-let [user (rf/subscribe [:users/user])]
    [base
     [breadcrumbs
      {:title "New user"
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Create user"]]
      [:div.panel-body
       [form-template]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:users/create-user @user])}
         "Create"]]]]]))

(defn edit-user-page []
  (r/with-let [user (rf/subscribe [:users/user])]
    [base
     [breadcrumbs
      {:title "Edit user"
       :active? true}]
     [:div.panel.panel-default
      [:div.panel-heading
       [:h2 "Edit user"]]
      [:div.panel-body
       [form-template]
       [:div.col-sm-offset-2.col-sm-10
        [:button.btn.btn-primary
         {:on-click #(rf/dispatch [:users/update-user @user])}
         "Update"]]]]]))


(defn list-users-table [users]
  [:table.table
   [:thead
    [:tr
     [:th "ID"]
     [:th "Email"]
     [:th "Admin?"]
     [:th "Edit"]
     [:th "Delete"]]]
   [:tbody
    (for [{:keys [user-id email admin] :as user} @users]
      ^{:key (:user-id user)}
      [:tr
       [:td user-id]
       [:td email]
       [:td (str admin)]
       [:td [:a.btn.btn-link
             {:href (str "/users/" user-id "/edit")}
             [:i.glyphicon.glyphicon-edit]]]
       [:td [:button.btn.btn-link {:on-click #(rf/dispatch [:users/delete-user user-id])}
             [:i.glyphicon.glyphicon-remove]]]])]])


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
      (if (seq @users)
        [list-users-table users]
        [:ul.list-group
         [:li.list-group-item "No users yet."]])]]))
