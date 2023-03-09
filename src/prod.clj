(ns prod
  (:gen-class)
  (:require app.todo-list ; in prod, load app into server so it can accept clients
            hyperfiddle.electric-jetty-server))

(def electric-server-config
  {:host "0.0.0.0", :port 8080, :resources-path "public"})

(defn -main [& args]
  (hyperfiddle.electric-jetty-server/start-server! electric-server-config))

; On CLJS side we reuse src/user.cljs for prod entrypoint