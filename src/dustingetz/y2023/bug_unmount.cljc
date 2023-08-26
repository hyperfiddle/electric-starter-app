(ns dustingetz.y2023.bug-unmount
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui]
            [missionary.core :as m]))

(e/defn Foo [v visible]
  (e/on-unmount #(println "unmounting Foo" v visible)))

(e/defn Bug-unmount []
  (let [!visible (atom [1])
        visible (e/watch !visible)]
    (ui/button (e/fn [] (swap! !visible (fn [v] (conj v (inc (last v))))))
      (dom/text "append"))
    (ui/button (e/fn [] (swap! !visible (fn [v] (into [] (butlast v)))))
      (dom/text "remove"))
    (e/for-by identity [v visible]
      (dom/div (dom/text v))
      (e/on-unmount #(println "unmounting branch")) ; no closure
      (Foo. v visible)))) 