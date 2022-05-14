(ns devkit
    #?(:clj (:require [hyperfiddle.photon :as p]
                      [shadow.cljs.devtools.api :as shadow]
                      [shadow.cljs.devtools.server :as shadow-server])))

#?(:clj
   (defn main [& {:keys [build]}]
     ;; TODO: why do we need to load app.core here?
     ;; if we don't, I get `Unsupported form.` compiler error on rebuilds
     (require 'app.core)
     (shadow-server/start!) ; shadow serves nrepl and browser assets including entrypoint
     (shadow/watch build)
     (p/start-websocket-server! {:host "localhost" :port 8081})))
