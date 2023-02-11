(ns user ; Must be ".clj" file, Clojure doesn't auto-load user.cljc
  (:require hyperfiddle.electric-jetty-server
            shadow.cljs.devtools.api
            shadow.cljs.devtools.server))

; Userland Electric code is lazy loaded by the shadow build due to usage of :require-macros
; in all Electric source files.
; WARNING: make sure your REPL and shadow-cljs are sharing the same JVM!

(comment
  (main) ; Electric Clojure(JVM) REPL entrypoint
  (hyperfiddle.rcf/enable!) ; turn on RCF after all transitive deps have loaded
  (shadow.cljs.devtools.api/repl :dev) ; shadow server hosts the cljs repl
  ; connect a second REPL instance to it
  ; (DO NOT REUSE JVM REPL it will fail weirdly)
  (type 1))

(def electric-server-config
  {:host "0.0.0.0", :port 8080, :resources-path "resources/public"})

(defn main [& args]
  (println "Starting Electric compiler and server...")
  (shadow.cljs.devtools.server/start!) ; serves index.html as well
  (shadow.cljs.devtools.api/watch :dev) ; depends on shadow server
  (def server (hyperfiddle.electric-jetty-server/start-server! electric-server-config))
  (comment (.stop server)))

(when (= "true" (get (System/getenv) "HYPERFIDDLE_AUTO_BOOT"))
  (main))