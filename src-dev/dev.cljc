(ns ^:dev/always dev ; rebuild everything when any file changes. Will fix
  (:require [contrib.assert :refer [check]]
            [contrib.clojurex :refer [bindx]]
            #?(:clj [contrib.datomic-contrib :as dx])
            #?(:clj [clojure.tools.logging :as log])
            electric-fiddle.main
            #?(:clj [electric-fiddle.server :refer [start-server!]])
            [hyperfiddle :as hf]
            [hyperfiddle.electric :as e]
            [hyperfiddle.rcf :as rcf]

            #?(:clj [models.teeshirt-orders-datomic :as model])

            dustingetz.fiddles ; datomic
            datomic-browser.domain
            datomic-browser.fiddles ; datomic
            electric-tutorial.fiddles
            electric-starter-app.fiddles
            hfql-demo.fiddles)) ; datomic

(comment (-main)) ; repl entrypoint

#_(e/def domain-registry ['datomic-browser.fiddles])

; What if we hardcode this dev entrypoint for the fiddles we're working on today?
; thus can share models, for example

(e/defn MergedFiddleMain [ring-req]
  (e/server
    (let [[conn db] (model/init-datomic)]
      (bindx [e/http-request ring-req
              datomic-browser.domain/conn (check conn)
              datomic-browser.domain/db (check db)
              datomic-browser.domain/schema (check (new (dx/schema> datomic-browser.domain/db)))]
        (e/client
          (binding [hf/pages (merge
                               dustingetz.fiddles/fiddles
                               datomic-browser.fiddles/fiddles
                               electric-starter-app.fiddles/fiddles
                               electric-tutorial.fiddles/fiddles
                               hfql-demo.fiddles/fiddles)]
            (electric-fiddle.main/Main.)))))))

#_
(e/defn Inject-general [Main]
  ; parallel strategy - note domains deploy independently in prod,
  ; cross-domain dependency is forbidden
  (e/server
    (bindx [~@server-bindings]
      (e/client
        (bindx [~@client-bindings
                hf/pages fiddle-registry]
          (Main.)))))

  ; sequential strategy
  #_(binding [hf/pages fiddle-registry]
      (datomic-browser.fiddles/Inject. ; set bindings and also allocate resources
        (e/fn [Main]
          (datomic-browser.fiddles/Inject.
            (e/fn [Main]
              (Main.)))))))

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
       (alter-var-root #'config #(merge % args))
       (log/info (pr-str config))
       (log/info "Starting Electric compiler and server...") ; run after REPL redirects stdout
       (@shadow-start!)
       (@shadow-watch :dev)
       ; todo block until finished?
       (comment (@shadow-stop!))
       (def server (start-server! (fn [ring-req] (e/boot-server {} MergedFiddleMain ring-req)) config))
       (comment (.stop server))
       (rcf/enable!))))

#?(:cljs
   (do
     (def electric-entrypoint
       ; in dev, we setup a merged fiddle config,
       ; fiddles must all opt in to the shared routing strategy
       (e/boot-client {} MergedFiddleMain nil))

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
