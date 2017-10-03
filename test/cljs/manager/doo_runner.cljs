(ns manager.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [manager.core-test]))

(doo-tests 'manager.core-test)

