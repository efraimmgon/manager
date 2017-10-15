(ns manager.handlers.features
  (:require
   [ajax.core :as ajax]
   [manager.db :as db]
   [manager.routes :refer [navigate!]]
   [re-frame.core :refer [dispatch reg-event-db reg-event-fx reg-sub]]))

(defn query [db [event-id]]
  (event-id db))

(defn gen-features [n]
  (for [i (range 1 (inc n))]
    {:feature-id i
     :title (str "feature " i)
     :description "Nostrud occaecat ad ut veniam incididunt laborum elit anim."}))

; ------------------------------------------------------------------------------
; Subs
; ------------------------------------------------------------------------------

(reg-sub :feature query)

(reg-sub :features query)

; ------------------------------------------------------------------------------
; Events
; ------------------------------------------------------------------------------

(reg-event-db
 :close-feature
 (fn [db _]
   (dissoc db :feature)))

(reg-event-fx
 :create-feature
 (fn [{:keys [db]} [_ feature]]
   ; POST feature params from DB
   ; after the key is returned from the DB:
   (navigate! (str "/projects/" (:project-id feature)
                   "/features/" 1))))

(reg-event-fx
 :delete-feature
 (fn [{:keys [db]} [_ feature-id]]
   ; DELETE feature key from DB, then:
   (navigate! (str "/projects/" (get-in db [:project :project-id])))))

(reg-event-fx
 :edit-feature
 (fn [{:keys [db]} [_ feature]]
   ; PUT feature params to server, then:
   (navigate! (str "/projects/" (get-in db [:project :project-id])))))

(reg-event-fx
 :load-feature
 (fn [{:keys [db]} [_ feature-id]]
   ; GET feature by id
   {:db (assoc db :feature (first (gen-features 1)))}))

(reg-event-fx
 :load-features-for
 (fn [{:keys [db]} [_ project-id]]
   ; GET features for project-id
   {:db (assoc db :features (gen-features 5))}))

(reg-event-db
 :set-active-feature
 (fn [db [_ feature-id]]
   (assoc db :feature feature-id)))
