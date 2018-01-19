(ns manager.utils)

(defn temp-id? [x]
  (keyword? x))

(defn done? [x]
  (= :done (:status x)))

(defn story-estimate [feat]
  (->> (:tasks feat)
       (filter (comp #{:done} :status))
       (map :curr-est)
       (reduce +)))
