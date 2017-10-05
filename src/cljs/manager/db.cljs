(ns manager.db)

(def default-db
  {:page :home
   :projects
   {1 {:id 1
       :title "foo project"
       :tasks
       (reduce
        (fn [acc item]
          (assoc acc (:id item) item))
        (sorted-map)
        (for [i (range 1 10)]
          {:id i
           :feature (str "feature " i)
           :task (str "task " i)
           :orig-est (rand-int 17)
           :curr-est (rand-int 17)
           :priority (inc (rand-int 8))
           :elapsed 0
           :remain 0
           :status "-"}))}}})
