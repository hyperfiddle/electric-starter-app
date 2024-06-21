(ns electric-starter-app.main
  (:require [hyperfiddle.electric-de :as e :refer [$]]
            [hyperfiddle.electric-dom3 :as dom]
            [electric-starter-app.toggle :as toggle]
            [electric-starter-app.chat :as chat]
            [electric-starter-app.component-lifecycle :as lifecycle]
            [electric-starter-app.system-properties :as system-props]))

(e/defn Main [ring-request]
  (binding [dom/node ($ dom/Root js/document.body)]
    #_($ toggle/Toggle)
    #_($ system-props/SystemProperties)
    #_($ chat/Chat)
    ($ lifecycle/LifeCycle)))
