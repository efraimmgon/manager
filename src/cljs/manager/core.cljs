(ns manager.core
  (:require
   [reagent.core :as r :refer [atom]]
   [re-frame.core :as rf]
   [manager.ajax :refer [load-interceptors!]]
   [manager.routes :refer [hook-browser-navigation!]]
   [manager.views :refer [page]]))


; ------------------------------------------------------------------------------
; Initialize app
; ------------------------------------------------------------------------------

(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (hook-browser-navigation!)
  (mount-components))
