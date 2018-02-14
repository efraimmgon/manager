(ns manager.components
  (:require
   [cljs.reader :as reader]
   [clojure.string :as string]
   [manager.utils :refer [datetime-format]]
   [reagent.core :as r :refer [atom]]
   [re-frame.core :as rf]
   [stand-lib.comps.forms :refer [input]]))

; ------------------------------------------------------------------------------
; Forms
; ------------------------------------------------------------------------------

(defn form-group [label & input]
  [:div.form-group
   [:label.col-sm-2.control-label label]
   (into
    [:div.col-sm-10]
    input)])

(defn datetime-input-group
  "Takes a path to where to save the input."
  [path]
  (r/create-class
   {:display-name "deadline component"
    :reagent-render
    (fn [path]
      [:div.input-group.date
       [input {:type :text
               :class "form-control"
               :placeholder "No date"
               :name path}]
       [:div.input-group-addon
        [:i.glyphicon.glyphicon-calendar]]])
    :component-did-mount
    (fn [this]
      (.datepicker (js/$ (r/dom-node this))
                   (clj->js {:format datetime-format}))
      (-> (.datepicker (js/$ (r/dom-node this)))
          (.on "changeDate"
               #(let [d (.datepicker (js/$ (r/dom-node this))
                                     "getDate")]
                 (rf/dispatch [:set-state path
                               (.toISOString d)])))))}))

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
