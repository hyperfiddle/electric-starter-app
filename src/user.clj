(ns user)

; Don't slow down REPL startup
(def shadow-start! (delay @(requiring-resolve 'shadow.cljs.devtools.server/start!)))
(def shadow-watch (delay @(requiring-resolve 'shadow.cljs.devtools.api/watch)))
(def photon-start-websocket-server! (delay @(requiring-resolve 'hyperfiddle.photon/start-websocket-server!)))

(defn main [& args]
  (@shadow-start!)                                          ; shadow serves nrepl and index.html as well
  (@shadow-watch :app)                                      ; depends on shadow server
  (def server (@photon-start-websocket-server! {:host "localhost" :port 8081}))
  (comment (.stop server)))

(comment
  "Photon REPL entrypoint"
  (main)
  )
