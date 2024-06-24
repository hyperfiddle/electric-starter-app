(ns electric-starter-app.two-clocks
  (:require
   [hyperfiddle.electric-de :as e :refer [$]]
   [hyperfiddle.electric-dom3 :as dom]))

(e/defn TwoClocks []
  (let [c (e/client ($ e/SystemTimeMs))
        s (e/server ($ e/SystemTimeMs))]

    (dom/div (dom/text "client time: " c))
    (dom/div (dom/text "server time: " s))
    (dom/div (dom/text "difference: " (- s c)))))
