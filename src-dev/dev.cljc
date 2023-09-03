(ns ^:dev/always dev ; rebuild everything when any file changes. Will fix
  (:require #?(:clj [clojure.tools.logging :as log])
            electric-fiddle.main
            #?(:clj [electric-fiddle.server :refer [start-server!]])
            [hyperfiddle :as hf]
            [hyperfiddle.electric :as e]
            [hyperfiddle.rcf :as rcf]

            dustingetz.fiddles ; datomic
            electric-tutorial.fiddles
            hfql-demo.fiddles)) ; datomic

(comment "repl entrypoint:" (-main)
  )

(e/def fiddle-registry
  (merge
    dustingetz.fiddles/fiddles
    electric-tutorial.fiddles/fiddles
    hfql-demo.fiddles/fiddles))

#?(:clj
   (do
     ; lazy load heavy dependencies for faster REPL startup
     (def shadow-start! (delay @(requiring-resolve 'shadow.cljs.devtools.server/start!)))
     (def shadow-stop! (delay @(requiring-resolve 'shadow.cljs.devtools.server/stop!)))
     (def shadow-watch (delay @(requiring-resolve 'shadow.cljs.devtools.api/watch)))
     
     (def config
       {:host "0.0.0.0", :port 8080,
        :resources-path "public"
        :manifest-path "public/js/manifest.edn"}) ; shadow build manifest

     (declare server)

     (defn -main [& args]
       (log/info (pr-str args))
       (alter-var-root #'config #(merge % args))
       (log/info "Starting Electric compiler and server...") ; run after REPL redirects stdout
       (@shadow-start!)
       (@shadow-watch :dev)
       ; todo block until finished?
       (comment (@shadow-stop!))
       (def server (start-server! config))
       (comment (.stop server))
       (rcf/enable!))))

#?(:cljs
   (do
     (def electric-entrypoint (e/boot
                                (binding [hf/pages fiddle-registry]
                                  (electric-fiddle.main/Main.))))
     (defonce reactor nil)

     (defn ^:dev/after-load ^:export start! []
       (set! reactor (electric-entrypoint
                       #(js/console.log "Reactor success:" %)
                       #(js/console.error "Reactor failure:" %)))
       (hyperfiddle.rcf/enable!))

     (defn ^:dev/before-load stop! []
       (when reactor (reactor)) ; teardown
       (set! reactor nil))))

(comment
  "CI tests"
  (alter-var-root #'hyperfiddle.rcf/*generate-tests* (constantly false))
  (hyperfiddle.rcf/enable!)
  (require 'clojure.test)
  (clojure.test/run-all-tests #"(hyperfiddle.api|user.orders)"))

(comment
  "Performance profiling, use :profile deps alias"
  (require '[clj-async-profiler.core :as prof])
  (prof/serve-files 8082)
            ;; Navigate to http://localhost:8082
  (prof/start {:framebuf 10000000})
  (prof/stop))