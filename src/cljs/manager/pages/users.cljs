(ns manager.pages.users
  (:require
   [manager.components :refer [base breadcrumbs form-group]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [stand-lib.comps.forms :refer [input textarea select]]
   [stand-lib.components :refer [pretty-display]]))


(defn checkbox-single-input
  [attrs]
  (let [field-value (rf/subscribe [:query (:name attrs)])
        f (fn [acc] (if (nil? acc) (:value attrs) nil))
        edited-attrs
        (-> attrs
            (assoc :type :checkbox)
            (update :on-change #(or % (fn [e] (rf/dispatch [:update-state (:name attrs) f]))))
            (assoc :checked (or (:default-checked attrs)
                                (= (:value attrs) @field-value)))
            (dissoc :default-checked))]
    ;; persist value when it's the default
    (when (and (:checked edited-attrs)
               (not (= @field-value (:value attrs))))
      (rf/dispatch [:update-state (:name attrs) f]))
    [:input edited-attrs]))

; link to users
; load users
; render page

(defn form-template []
  (r/with-let [user (rf/subscribe [:users/user])
               ns [:users :user]]
    (when-not (:user-id @user)
      (rf/dispatch [:set-state (conj ns :is-active) true]))
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
      [checkbox-single-input {:value true
                              :class "form-control"
                              :name (conj ns :is-active)}]]
     [form-group
      "Admin?"
      [checkbox-single-input {:value true
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
                 :disabled true}]])]))

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
