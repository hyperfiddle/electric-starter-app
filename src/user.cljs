(ns user
  (:require [app.core]
            [hyperfiddle.photon :as p])
  (:import [hyperfiddle.photon Pending]
           [missionary Cancelled]))

(def main (p/client (p/main (try (app.core/App.)
                                 (catch Pending _)
                                 (catch Cancelled _)))))
(def reactor)
(defn ^:dev/after-load start! [] (set! reactor (main js/console.log js/console.error)))
;; TODO: keep seeing `missionary.Cancelled {message: 'Watch cancelled.'}` on the js console
(defn ^:dev/before-load stop! [] (when reactor (reactor)) (set! reactor nil))

