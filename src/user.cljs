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

(defn ^:dev/after-load start! []
  (set! reactor (main js/console.log js/console.error)))

(defn ^:dev/before-load stop! []
  (when reactor (reactor))                                  ; teardown
  (.. js/document (getElementById "root") (replaceChildren)) ; temporary workaround for https://github.com/hyperfiddle/photon/issues/10
  (set! reactor nil))
