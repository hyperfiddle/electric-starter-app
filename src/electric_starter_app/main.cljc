(ns electric-starter-app.main
  (:require [hyperfiddle.electric-de :as e :refer [$]]
            [hyperfiddle.electric.impl.lang-de2 :as lang]
            [hyperfiddle.electric-dom3-event-handling :as eh]
            [hyperfiddle.electric-dom3 :as dom]))

;; Saving this file will automatically recompile and update in your browser

(e/defn Main [ring-request]
  (e/client (prn (e/server "SERVER"))))
