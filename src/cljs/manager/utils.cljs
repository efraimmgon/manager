(ns manager.utils
  (:require
   [cljs.spec.alpha :as s]
   [cljs-time.core :as t]
   [cljs-time.format :as tf]
   [reagent.core :as r]
   [re-frame.core :as rf]))

(defn temp-id? [x]
  (keyword? x))

(defn done? [x]
  (= "done" (:status x)))

(defn story-estimate [feat]
  (->> (:tasks feat)
       (filter (comp #{"done"} :status))
       (map :curr-est)
       (reduce +)))

(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def check-spec-interceptor
  (rf/after
   (partial check-and-throw :manager.db/db)))

(def interceptors
  [
   (when ^boolean js/goog.DEBUG rf/debug)
   rf/trim-v])

(defn full-name-or-email [user]
  (let [name
        (str (:first-name user) " "
             (:last-name user))]
    (if (clojure.string/blank? name)
      (:email user)
      name)))

; ------------------------------------------------------------------------------
; Datetime
; ------------------------------------------------------------------------------
(defn parse-date
  "Takes a string and parses it to an instance of cljs-time.core/date-time.
  :args (s/cat :format keyword? :s string?), where :s is a
  (.toISOString (js/Date.))
  :ret #(cljs-time.core/date? %)"
  [format s]
  (let [formats {:date-time :date-time}]
    (tf/parse (tf/formatters (get formats format))
              s)))

(defn deadline-status
  "Takes a (.toISOString (js/Date.)) and checks whats the deadline status.
  :ret #{:expired :warning :on-schedule}"
  [deadline]
  (when deadline
    (let [now (t/now)
          deadline (parse-date :date-time deadline)]
      (cond
        (t/before? deadline now)
        :expired
        (t/before? deadline (t/plus now (t/days 7)))
        :warning
        :else :on-schedule))))

(def datetime-format "yyyy-mm-ddT03:00:00.000Z")
