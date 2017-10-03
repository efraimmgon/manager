(ns ^:figwheel-no-load manager.app
  (:require [manager.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
