(ns manager.db)

(defn gen-tasks [n]
  (for [i (range 1 (inc n))]
    {:id i
     :feature (str "feature " i)
     :task (str "task " i)
     :orig-est (rand-int 17)
     :curr-est (rand-int 17)
     :priority (inc (rand-int 8))
     :elapsed 0
     :remain 0
     :status "-"}))

(def default-db
  {:page :home
   :projects
   {1 {:id 1
       :title "foo project"
       :tasks (sort-by :priority (gen-tasks 10))}}})
