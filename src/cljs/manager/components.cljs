(ns manager.components
  (:require [cljs.reader :as reader]))

; --------------------------------------------------------------------
; Debugging
; --------------------------------------------------------------------

(defn pretty-display [title data]
  [:div
   [:h3 title]
   [:pre
    (with-out-str
     (cljs.pprint/pprint @data))]])

; ------------------------------------------------------------------------------
; Forms
; ------------------------------------------------------------------------------

; Helpers ----------------------------------------------------------------------

(defn- default-attrs [attrs fields]
  {:on-change #(swap! fields assoc (:name attrs) (-> % .-target .-value))
   :value ((:name attrs) @fields)})

(defn- date-input [attrs fields]
  (let [date ((:name attrs) @fields)]
    [:input
     (if date
       (assoc attrs :value (-> ((:name attrs) @fields)
                               (.split "T")
                               (first)))
       attrs)]))

(defn- radio-input [attrs fields]
  (let [attrs (-> (default-attrs attrs fields)
                  ;; `(-> % .-target .-value)` returns strings no matter what
                  ;; we're trying to do go around that
                  (assoc :on-change #(swap! fields assoc (:name attrs) (-> % .-target .-value reader/read-string)))
                  (merge attrs))]
    [:input attrs]))

; Core -------------------------------------------------------------------------

(defn form-group [label & input]
  [:div.form-group
   [:label.col-sm-2.control-label label]
   (into
    [:div.col-sm-10]
    input)])

(defn input [attrs fields]
  (let [attrs (merge (default-attrs attrs fields) attrs)]
    (condp = (:type attrs)
           :date [date-input attrs fields]
           :radio [radio-input attrs fields]

           ;; default
           [:input attrs])))

(defn textarea [attrs fields]
  (let [attrs (merge (default-attrs attrs fields) attrs)]
    [:textarea attrs]))

(defn option-value [options val]
  (some #(and (= (:value %) val)
              val)
        (map second options)))

;;; with the current implementation it does not support options with
;;; strings as values.
(defn select [attrs fields & options]
  (let [attrs (-> (default-attrs attrs fields)
                  ;; `(-> % .-target .-value)` returns strings no matter what
                  ;; we're trying to do go around that
                  (assoc :on-change #(swap! fields assoc (:name attrs) (-> % .-target .-value reader/read-string)))
                  (merge attrs))
        default-val (-> options ffirst second :value)]
    ;; set fields' default value
    (when-not ((:name attrs) @fields)
      (swap! fields assoc (:name attrs) default-val))
    (into
     [:select
      ;; set select value
      (assoc attrs :value ((:name attrs) @fields))]
     options)))

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

(defn thead [headers]
  [:thead
   [:tr
    (for [th headers]
      ^{:key th}
      [:th.text-center th])]])

(defn tbody [rows]
  (into
   [:tbody]
   (for [row rows]
     (into
      [:tr]
      (for [td row]
        [:td.text-center td])))))

(defn thead-indexed
  "Coupled with `tbody-indexed`, allocates a col for the row's index."
  [headers]
  [:thead
   (into
     [:tr
      [:th.text-center "#"]]
     (for [th headers]
       [:th.text-center th]))])

(defn tbody-indexed
  "Coupled with `thead-indexed`, allocates a col for the row's index."
  [rows]
  (into
   [:tbody]
   (map-indexed
    (fn [i row]
      (into
       [:tr [:td.text-center (inc i)]]
       (for [td row]
         [:td.text-center
          td])))
    rows)))

(defn thead-editable [headers]
  [thead
   (conj headers "Edit" "Delete")])
