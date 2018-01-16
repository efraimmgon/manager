(ns manager.utils)

(defn temp-id? [x]
  (keyword? x))

(defn done? [x]
  (let [status (:status x)]
    (if (coll? status)
      (contains? status :done)
      (= :done status))))
