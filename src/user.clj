(ns user ; Must be ".clj" file, Clojure will not auto-run user.cljc
  (:refer-clojure :exclude [compile])
  (:require hyperfiddle.photon-jetty-server
            shadow.cljs.devtools.api
            shadow.cljs.devtools.server))

; Userland photon application code will be lazy loaded by the shadow build `(main)`
; due to :require-macros in all Photon source files.
; WARNING: make sure your REPL and shadow-cljs are sharing the same JVM!

(comment
  "Photon Clojure REPL entrypoint"
  (main)

  "ClojureScript REPL entrypoint"
  ; shadow server exports an repl, connect a second REPL instance to it (DO NOT REUSE JVM REPL it will fail weirdly)
  (shadow.cljs.devtools.api/repl :app)
  (type 1))

(def host "0.0.0.0")
(def port 8080)

(defn main "CLJ main" [& args]
  (println "Starting Photon compiler and server...")
  (shadow.cljs.devtools.server/start!) ; serves index.html as well as js target
  (shadow.cljs.devtools.api/watch :app) ; depends on shadow server
  ; todo report clearly if shadow build failed, i.e. due to yarn not being run
  (def server (hyperfiddle.photon-jetty-server/start-server! {:host host, :port port, :resources-path "resources/public"}))
  (println (str "\n👉 App server available at http://" host ":" (-> server (.getConnectors) first (.getPort)) "\n"))
  (comment (.stop server)))
