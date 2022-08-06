(ns user
  (:require [app.core]
            [hyperfiddle.photon :as p]
            [hyperfiddle.photon-dom :as dom])
  (:import [hyperfiddle.photon Pending]))

(def main (p/boot (try
                    (binding [dom/node (dom/by-id "root")]
                      (app.core/Todo-list.))
                    (catch Pending _))))
(def reactor)
(defn ^:dev/after-load start! [] (set! reactor (main js/console.log js/console.error)))
;; TODO: keep seeing `missionary.Cancelled {message: 'Watch cancelled.'}` on the js console
(defn ^:dev/before-load stop! [] (when reactor (reactor)) (set! reactor nil))
