(ns ^:dev/always user ; rebuild everything when any file changes. Will fix
  (:require hyperfiddle.electric
            hyperfiddle.rcf
            user-main))

(def electric-main (hyperfiddle.electric/boot (user-main/Main.)))
(defonce reactor nil)

(defn ^:dev/after-load ^:export start! []
  (set! reactor (electric-main
                 #(js/console.log "Reactor success:" %)
                 (fn [error]
                   (case (:hyperfiddle.electric/type (ex-data error))
                     :hyperfiddle.electric-client/stale-client 
                     (do (js/console.log "Client/server version mismatch, refreshing page.")
                       (.reload (.-location js/window))) 
                     (js/console.error "Reactor failure:" error)))))
  (hyperfiddle.rcf/enable!))

(defn ^:dev/before-load stop! []
  (when reactor (reactor)) ; teardown
  (set! reactor nil))