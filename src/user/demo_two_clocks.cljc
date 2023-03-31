(ns user.demo-two-clocks
  "Electric hello world: streaming lexical scope"
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

(e/defn TwoClocks []
  (e/client
    (let [c (e/client e/system-time-ms)
          s (e/server e/system-time-ms)]

      (dom/div (dom/text "client time: " c))
      (dom/div (dom/text "server time: " s))
      (dom/div (dom/text "skew: " (- s c))))))