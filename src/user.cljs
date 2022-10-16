(ns user
  (:require [app.core]
            [hyperfiddle.photon :as p]
            [hyperfiddle.photon-dom :as dom]
            hyperfiddle.photon.debug)
  (:import [hyperfiddle.photon Pending]
           [missionary Cancelled]))

(def main
  (p/boot
    (try
      (binding [dom/node (dom/by-id "root")]
        (app.core/Todo-list.))
      (catch Pending _)
      (catch Cancelled e (throw e))
      (catch :default err
        (js/console.error (str (ex-message err) "\n\n" (hyperfiddle.photon.debug/stack-trace p/trace)) err)))))

(defonce reactor nil)

(defn ^:dev/after-load ^:export start! []
  (assert (nil? reactor) "reactor already running")
  (set! reactor (main #(js/console.log "Reactor success:" %)
                      #(js/console.error "Reactor failure:" %))))

(defn ^:dev/before-load stop! []
  (when reactor (reactor)) ; teardown
  (set! reactor nil))