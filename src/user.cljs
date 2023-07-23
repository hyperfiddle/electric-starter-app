(ns ^:dev/always user ; rebuild everything when any file changes. Will fix
  (:require electric-fiddle.main
            [hyperfiddle.electric :as e]
            hyperfiddle.rcf))

(def electric-entrypoint (e/boot (electric-fiddle.main/Main.)))
(defonce reactor nil)

(defn ^:dev/after-load ^:export start! []
  (set! reactor (electric-entrypoint
                  #(js/console.log "Reactor success:" %)
                  #(js/console.error "Reactor failure:" %)))
  (hyperfiddle.rcf/enable!))

(defn ^:dev/before-load stop! []
  (when reactor (reactor)) ; teardown
  (set! reactor nil))