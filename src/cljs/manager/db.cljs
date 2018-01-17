(ns manager.db)

(def default-db
  {:page :home
   :status [[:done "Done"], [:pending "Pending" true]]
   :priorities [[:urgent "Urgent"], [:high "High"], [:important "Important"],
                [:medium "Medium"], [:moderate "Moderate"], [:low "Low"],
                [:dont-fix "Don't fix"]]
   :features {:show-completed? false}
   ;; dev only:
   :ls/features :manager/features
   :ls/tasks :manager/tasks})
