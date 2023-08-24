(ns user
  (:require [electric-fiddle.config :as config]
            [electric-fiddle.server :refer [start-server!]]
            [hyperfiddle.rcf :as rcf]))

(def shadow-start! (delay @(requiring-resolve 'shadow.cljs.devtools.server/start!)))
(def shadow-watch (delay @(requiring-resolve 'shadow.cljs.devtools.api/watch)))

(defn main [& args]
  (println "Starting Electric compiler and server...") ; run after REPL redirects stdout
  (do (@shadow-start!) (@shadow-watch :dev))
  (def server (start-server! config/electric-server-config))
  (comment (.stop server)))

(comment
  (do (@shadow-start!) (@shadow-watch :dev))
  ; wait for shadow to finish
  (main)
  (rcf/enable!)
  (rcf/enable! false))
