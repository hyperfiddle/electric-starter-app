(ns prod
  (:gen-class)
  (:require [electric-fiddle.config :as config]
            electric-fiddle.main
            [electric-fiddle.server :refer [start-server!]]))

#?(:clj
   (defn -main [& args] ; https://clojure.org/reference/repl_and_main
     (start-server! config/electric-server-config)))

#?(:cljs
   (do
     (def electric-entrypoint (e/boot (electric-fiddle.main/Main.)))
     (defonce reactor nil)

     (defn ^:dev/after-load ^:export start! []
       (set! reactor (electric-entrypoint
                       #(js/console.log "Reactor success:" %)
                       #(js/console.error "Reactor failure:" %)))
       #_(hyperfiddle.rcf/enable!))

     (defn ^:dev/before-load stop! []
       (when reactor (reactor)) ; teardown
       (set! reactor nil))))