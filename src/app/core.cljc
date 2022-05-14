(ns app.core
  (:require [hyperfiddle.photon :as p]
            [hyperfiddle.photon-dom :as dom]))

(p/defn Foo []
  (dom/div
    (dom/text ~@ (pr-str (type 1))))) ; ~@ marks client/server transfer

(def app
  #?(:cljs
     (p/client
      (p/main
       (binding [dom/parent (dom/by-id "root")]
         (dom/div
          (dom/attribute "id" "main")
          (dom/class "browser")
          (dom/div
           (dom/class "view")
           (Foo.))))))))

#?(:cljs
   (do
     (def reactor)
     (defn ^:dev/after-load start! [] (set! reactor (app js/console.log js/console.error)))
     ;; TODO: keep seeing `missionary.Cancelled {message: 'Watch cancelled.'}` on the js console
     (defn ^:dev/before-load stop! [] (when reactor (reactor)) (set! reactor nil))))
