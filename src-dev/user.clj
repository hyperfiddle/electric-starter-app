(ns user
  (:require [electric-fiddle.config :as config]
            [electric-fiddle.server :refer [start-server!]]
            [hyperfiddle.rcf :as rcf]))

(defn main [& args]
  (println "Starting Electric compiler and server...") ; run after REPL redirects stdout
  (def server (start-server! config/electric-server-config))
  (comment (.stop server)))

(comment
  (def shadow-start! (delay @(requiring-resolve 'shadow.cljs.devtools.server/start!)))
  (def shadow-watch (delay @(requiring-resolve 'shadow.cljs.devtools.api/watch)))
  (do (@shadow-start!) (@shadow-watch :dev))
  ; wait for shadow to finish
  (main)
  (rcf/enable!)
  (rcf/enable! false))
