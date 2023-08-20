(ns prod
  (:gen-class)
  (:require [electric-fiddle.config :as config]
            electric-fiddle.main ; in prod, load app into server so it can accept clients
            [electric-fiddle.server :refer [start-server!]]))

(defn -main [& args] ; run with `clj -M -m prod`
  (start-server! config/electric-server-config))

; On CLJS side we reuse src/user.cljs for prod entrypoint