(ns manager.utils
  (:require
   [cljs.spec.alpha :as s]
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
  [check-spec-interceptor
   (when ^boolean js/goog.DEBUG rf/debug)
   rf/trim-v])

(defn full-name-or-email [user]
  (let [name
        (str (:first-name user) " "
             (:last-name user))]
    (if (clojure.string/blank? name)
      (:email user)
      name)))
