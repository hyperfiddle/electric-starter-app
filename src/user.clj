(ns user
  (:require hyperfiddle.photon-jetty-server
            shadow.cljs.devtools.api
            shadow.cljs.devtools.server))

(def host "0.0.0.0")
(def port 8080)

(defn main [& args]
  (shadow.cljs.devtools.server/start!)
  (shadow.cljs.devtools.api/watch :app)
  (def server (hyperfiddle.photon-jetty-server/start-server! {:host host, :port port, :resources-path "resources/public"}))
  (comment (.stop server))
  (println (str "\n👉 App available at http://" host ":" (-> server (.getConnectors) first (.getPort)) "\n")))

(comment
  "Photon REPL entrypoint"
  (main)

  "Enable RCF"
  (hyperfiddle.rcf/enable!))
