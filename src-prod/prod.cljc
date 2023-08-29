(ns prod
  #?(:clj (:gen-class))
  (:require [electric-fiddle.config :as config]
            electric-fiddle.main
            #?(:clj [electric-fiddle.server :refer [start-server!]])
            [hyperfiddle.electric :as e]
            
            electric-tutorial.tutorial-registry
            dustingetz.dustingetz-registry))

(e/def fiddle-registry ; Electric needs to see this at client compile time
  (merge
    electric-tutorial.tutorial-registry/pages
    dustingetz.dustingetz-registry/pages))

#?(:clj
   (defn -main [& args] ; https://clojure.org/reference/repl_and_main
     (start-server! config/electric-server-config)))

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
       #_(hyperfiddle.rcf/enable!))

     (defn ^:dev/before-load stop! []
       (when reactor (reactor)) ; teardown
       (set! reactor nil))))