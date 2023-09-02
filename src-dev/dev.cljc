(ns ^:dev/always dev ; rebuild everything when any file changes. Will fix
  (:require #?(:clj [clojure.tools.logging :as log])
            [electric-fiddle.config :as config]
            electric-fiddle.main
            #?(:clj [electric-fiddle.server :refer [start-server!]])
            [hyperfiddle.electric :as e]
            [hyperfiddle.rcf :as rcf]
            
            dustingetz.fiddles ; datomic
            electric-tutorial.fiddles
            hfql-demo.fiddles)) ; datomic

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

     (declare server)

     (defn -main [& args]
       (log/info `-main "args: " (pr-str args))
       (log/info "Starting Electric compiler and server...") ; run after REPL redirects stdout
       (do (@shadow-start!) (@shadow-watch :dev))
       (comment (@shadow-stop!))
       (def server (start-server! config/config))
       (comment (.stop server)))

     (comment
       (do (@shadow-start!) (@shadow-watch :dev))
       ; wait for shadow to finish
       (-main)
       (rcf/enable!))))

#?(:cljs
   (do
     (def electric-entrypoint (e/boot
                                (binding [config/pages fiddle-registry]
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