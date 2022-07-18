(ns app.core
  (:require [hyperfiddle.photon :as p]
            [hyperfiddle.photon-dom :as dom]
            [hyperfiddle.zero :as z]
            [missionary.core :as m])
  (:import (hyperfiddle.photon Pending))
  #?(:cljs (:require-macros app.core)))                     ; forces shadow hot reload to also reload JVM at the same time

(p/defn App []
  (dom/div
    (dom/h1 (dom/text "Healthcheck"))

    (dom/p (dom/span (dom/text "millisecond time: "))
           (dom/span (dom/text z/time)))

    (let [x (dom/button {:type "button"}
                        (dom/text "click me")
              (new (->> (dom/events dom/parent "click")
                        (m/eduction (map (constantly 1)))
                        (m/reductions + 0))))]

      (dom/div
        (dom/table
          (dom/thead
           (dom/td {:width "5em"} (dom/text "count"))
           (dom/td {:width "10em"} (dom/text "type")))
          (dom/tbody
            (dom/tr
              (dom/td (dom/text (str x)))
              (dom/td (dom/text (if (odd? x)
                                  ~@(pr-str (type x))       ; ~@ marks client/server transfer
                                  (pr-str (type x))))))))))))

(def app #?(:cljs (p/client (p/main
                               (binding [dom/parent (dom/by-id "root")]
                                 (try
                                   (App.)
                                   (catch Pending _)))))))
