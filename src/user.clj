(ns user
  (:require [server :refer [start-server!]]
            [shadow.cljs.devtools.api :as shadow]
            [shadow.cljs.devtools.server :as shadow-server]))

(def host "0.0.0.0")
(def port 8080)

(defn main [{:keys [mode]}]
  (if (= :single-run mode)
    (shadow/compile :app)
    (do (shadow-server/start!)
        (shadow/watch :app)))
  (def server (start-server! {:host host, :port port}))
  (println (str "\n👉 App available at http://" host ":" (-> server (.getConnectors) first (.getPort))
             "\n")))

(comment
  (.stop server)
  )

(comment
  "Photon REPL entrypoint"
  (main {})
  )
