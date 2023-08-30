(ns prod
  (:require [contrib.assert :refer [check]]
            #?(:clj [clojure.tools.logging :as log])
            [electric-fiddle.config :as config :refer [config]]
            electric-fiddle.main
            #?(:clj [electric-fiddle.server :refer [start-server!]])
            [hyperfiddle :as hf]
            [hyperfiddle.electric :as e]
            #?(:cljs #=(clojure.core/identity electric-fiddle.config/*hyperfiddle-user-ns*))))

#?(:clj
   (defn -main [& {:strs [] :as args}] ; clojure.main entrypoint, args are strings
     (alter-var-root #'config #(merge % args))
     (log/info (pr-str config))
     (require (symbol (str (::hf/domain config) ".fiddles"))) ; load userland server
     (start-server! config)))

#?(:cljs
   (do
     (def electric-entrypoint
       (e/boot
         (binding [config/pages (config/install-fiddles)]
           (electric-fiddle.main/Main.))))
     
     (defonce reactor nil)

     (defn ^:dev/after-load ^:export start! []
       (set! reactor (electric-entrypoint
                       #(js/console.log "Reactor success:" %)
                       #(js/console.error "Reactor failure:" %))))

     (defn ^:dev/before-load stop! []
       (when reactor (reactor)) ; teardown
       (set! reactor nil))))