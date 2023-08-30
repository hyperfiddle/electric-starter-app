(ns prod
  (:require #?(:clj [clojure.tools.logging :as log])
            [electric-fiddle.config :as config]
            electric-fiddle.main
            #?(:clj [electric-fiddle.server :refer [start-server!]])
            [hyperfiddle.electric :as e]
            #?(:cljs #=(clojure.core/identity electric-fiddle.config/*hyperfiddle-user-ns*))))

#?(:clj
   (defn -main [& {:strs [domain] :as args}] ; https://clojure.org/reference/repl_and_main
     (log/info (pr-str args))
     (log/info "domain: " (pr-str domain))
     (require (symbol (str domain ".fiddles"))) ; load userland server
     (start-server! config/electric-server-config)))

#?(:cljs
   (do
     (def electric-entrypoint (e/boot (electric-fiddle.main/Main.)))
     
     (defonce reactor nil)

     (defn ^:dev/after-load ^:export start! []
       (set! reactor (electric-entrypoint
                       #(js/console.log "Reactor success:" %)
                       #(js/console.error "Reactor failure:" %))))

     (defn ^:dev/before-load stop! []
       (when reactor (reactor)) ; teardown
       (set! reactor nil))))


; clj -X:build:prod:dustingetz build-client :build :prod-dustingetz
; clj -X:build:prod:dustingetz build-client :build :prod-dustingetz :debug true :verbose true :optimizations false
; clj -M:prod:dustingetz -m prod
; clj -M:prod:dustingetz -m prod :domain dustingetz   

; clj -X:build:prod:hello-fiddle build-client :build :hello-fiddle :debug true :verbose true :optimizations false
; clj -M:prod:dustingetz -m prod domain hello-fiddle