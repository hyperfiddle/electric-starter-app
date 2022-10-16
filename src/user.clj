(ns user
  "Start a REPL with `clj -A:dev`, or jack in with :dev alias."
  (:refer-clojure :exclude [compile])
                                        ; For rapid REPL startup, put absolute minimum of requires here: REPL conveniences only,
                                        ; which includes clojure reader extensions listed in data_readers.cljc.
  (:require hyperfiddle.rcf))

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

(def cljs-eval (delay @(requiring-resolve 'shadow.cljs.devtools.api/cljs-eval)))
(def shadow-start! (delay @(requiring-resolve 'shadow.cljs.devtools.server/start!)))
(def shadow-watch (delay @(requiring-resolve 'shadow.cljs.devtools.api/watch)))
(def shadow-compile (delay @(requiring-resolve 'shadow.cljs.devtools.api/compile)))
(def shadow-release (delay @(requiring-resolve 'shadow.cljs.devtools.api/release)))
(def start-server! (delay @(requiring-resolve 'hyperfiddle.photon-jetty-server/start-server!)))
(def rcf-enable! (delay @(requiring-resolve 'hyperfiddle.rcf/enable!)))

(def host "0.0.0.0")
(def port 8080)

(defn serve! "Start Photon app server" []
  (let [host "0.0.0.0"]
    (def server (@start-server! {:host host, :port 8080, :resources-path "resources/public"}))
    (println (str "\n👉 App server available at http://" host ":" (-> server (.getConnectors) first (.getPort))
                  "\n"))))

(defn main "CLJ main" [& args]
  (println "Starting Photon compiler and server...")
  (@shadow-start!)                                         ; serves index.html as well
  (@rcf-enable! false) ; don't run cljs tests on compile - in case user enabled at the REPL
  (@shadow-watch :app)                                   ; depends on shadow server
  ; todo report clearly if shadow build failed, i.e. due to yarn not being run
  (serve!)
  (comment (.stop server))
  (@rcf-enable!))

(defn compile []
  ; optimized artifact but with debug information available to find problems
  (@shadow-compile :app))

(defn release []
  ; optimized artifact but with debug information available to find problems
  (@shadow-release :app {:debug true}))

(defn boot! []
  (compile)
  (serve!))

(when (contains? (System/getenv) "HYPERFIDDLE_DEV")
  "auto boot"
  (main)) ; this blocks the repl until build is ready. alternatively can run in a future?

(defn rcf-shadow-hook {:shadow.build/stage #{:compile-prepare :compile-finish}}
  [build-state & args]
  (case (:shadow.build/stage build-state)
    :compile-prepare (@rcf-enable! false)
    :compile-finish  (@rcf-enable!))
  build-state)
