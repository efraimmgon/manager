(ns manager.utils)

(defn temp-id? [x]
  (keyword? x))

(defn done? [x]
  (= :done (:status x)))
