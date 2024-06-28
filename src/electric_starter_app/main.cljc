(ns electric-starter-app.main
  (:require [hyperfiddle.electric-de :as e :refer [$]]
            [hyperfiddle.electric-dom3 :as dom]
            [electric-starter-app.toggle :as toggle]
            [electric-starter-app.chat :as chat]
            [electric-starter-app.svg :as svg]
            [electric-starter-app.two-clocks :as two-clocks]
            [electric-starter-app.todos-simple :as todo]
            [electric-starter-app.webview :as webview]
            [electric-starter-app.seven-guis-1-counter :as counter]
            [electric-starter-app.seven-guis-2-temperature :as temperature]
            [electric-starter-app.seven-guis-4-timer :as timer]
            [electric-starter-app.seven-guis-7-crud :as crud]
            [electric-starter-app.component-lifecycle :as lifecycle]
            [electric-starter-app.system-properties :as system-props]))

(e/defn Main [ring-request]
  (binding [dom/node js/document.body]
    (dom/div
      #_($ svg/SVG)
      #_($ todo/TodoList)
      #_($ crud/CRUD)
      #_($ timer/Timer)
      ($ temperature/TemperatureConverter)
      #_($ counter/Counter)
      #_($ webview/Webview)
      #_($ two-clocks/TwoClocks)
      #_($ toggle/Toggle)
      #_($ system-props/SystemProperties)
      #_($ chat/Chat)
      #_($ lifecycle/LifeCycle))))
