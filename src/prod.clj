(ns prod
  (:gen-class)
  (:require electric-fiddle.main ; in prod, load app into server so it can accept clients
            [electric-server-java11-jetty10 :refer [start-server!]]))

(def electric-server-config
  {:host "0.0.0.0", :port 8080, :resources-path "public"})

(defn -main [& args] ; run with `clj -M -m prod`
  (start-server! electric-server-config))

; On CLJS side we reuse src/user.cljs for prod entrypoint