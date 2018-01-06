(ns manager.components
  (:require
   [cljs.reader :as reader]
   [clojure.string :as string]
   [reagent.core :as r :refer [atom]]
   [re-frame.core :as rf]))


; ------------------------------------------------------------------------------
; Forms
; ------------------------------------------------------------------------------

(defn form-group [label & input]
  [:div.form-group
   [:label.col-sm-2.control-label label]
   (into
    [:div.col-sm-10]
    input)])

; --------------------------------------------------------------------
; MISC
; --------------------------------------------------------------------

(defn breadcrumbs [& items]
  (into
   [:ol.breadcrumb
    [:li [:a {:href "/"} "Home"]]]
   (for [{:keys [href title active?] :as item} items]
     (if active?
       [:li.active title]
       [:li [:a {:href href} title]]))))

(defn base [& body]
  (into
   [:div.container]
   body))
