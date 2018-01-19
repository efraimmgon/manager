(ns manager.db)

(def default-db
  {:page :home
   :status [{:id "done" :name "done" :idx 1},
            {:id "pending" :name "pending" :default? true :idx 2}]
   :priorities [{:id :urgent, :name "urgent" :idx 1},
                {:id :high, :name "high" :idx 2},
                {:id :important, :name "Important" :idx 3},
                {:id :medium, :name "Medium" :idx 4},
                {:id :moderate, :name "Moderate" :idx 5},
                {:id :low, :name "Low" :idx 6},
                {:id :dont-fix, :name "Don't fix" :idx 7}]
   :types [{:name "feature", :idx 1}
           {:name "bug", :idx 2}
           {:name "chore", :idx 3}]
   :stories {:show-completed? false}
   ;; dev only:
   :ls-stories :manager/stories
   :ls-tasks :manager/tasks})
