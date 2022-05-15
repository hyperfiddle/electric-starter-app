(ns devkit
    #?(:clj (:require [hyperfiddle.photon :as p]
                      [shadow.cljs.devtools.api :as shadow]
                      [shadow.cljs.devtools.server :as shadow-server])))

#?(:clj
   (defn main [& {:keys [build]}]
     (shadow-server/start!) ; shadow serves nrepl and browser assets including entrypoint
     (shadow/watch build)
     (p/start-websocket-server! {:host "localhost" :port 8081})))
