(ns user
  (:require [app.core]
            [hyperfiddle.photon :as p]
            [hyperfiddle.photon-dom :as dom])
  (:import [hyperfiddle.photon Pending]
           [missionary Cancelled]))

(p/defn App []
  (binding [dom/node (dom/by-id "root")]
    (p/remote (app.core/Todo-list.))))

(def main (p/boot (try (App.)
                       (catch Pending _)
                       (catch Cancelled _))))
(def reactor)
(defn ^:dev/after-load start! [] (set! reactor (main js/console.log js/console.error)))
;; TODO: keep seeing `missionary.Cancelled {message: 'Watch cancelled.'}` on the js console
(defn ^:dev/before-load stop! [] (when reactor (reactor)) (set! reactor nil))

