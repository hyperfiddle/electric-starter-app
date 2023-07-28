(ns user
  (:require [electric-server-java8-jetty9 :refer [start-server!]]
            [hyperfiddle.rcf :as rcf]))

(def electric-server-config 
  {:host "0.0.0.0", :port 8080, :resources-path "public", :manifest-path "public/js/manifest.edn"})

(defn main []
  (println "Starting Electric compiler and server...") ; run after REPL redirects stdout
  (def server (start-server! electric-server-config))
  (comment (.stop server)))

(comment
  (def shadow-start! (delay @(requiring-resolve 'shadow.cljs.devtools.server/start!)))
  (def shadow-watch (delay @(requiring-resolve 'shadow.cljs.devtools.api/watch)))
  (do (@shadow-start!) (@shadow-watch :dev))
  ; wait for shadow to finish
  (main)
  (rcf/enable!)
  (rcf/enable! false))
