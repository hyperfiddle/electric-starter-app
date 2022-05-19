(ns user
  (:require [hyperfiddle.photon :as p]
            #?(:clj [shadow.cljs.devtools.api :as shadow])
            #?(:clj [shadow.cljs.devtools.server :as shadow-server])
            app.core))

(comment
  "startup"
  (main))

#?(:clj
   (defn main [& args]
     (shadow-server/start!)                                 ; shadow serves nrepl and browser assets including index.html
     (shadow/watch :app)
     (p/start-websocket-server! {:host "localhost" :port 8081})))

#?(:cljs
   (do
     (defonce reactor nil)
     (defn ^:dev/after-load start! []
       (set! reactor (((ns-publics 'app.core) 'app)         ; re-resolve recompiled Photon main
                      #(js/console.log "Reactor success:" %)
                      #(js/console.error "Reactor failure:" %))))

     (defn ^:dev/before-load stop! []
       (when reactor (reactor))
       (set! reactor nil))))
