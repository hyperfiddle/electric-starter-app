(ns electric-tutorial.tutorial-backpressure
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

(e/defn Backpressure []
  (e/client 
    (let [c (e/client e/system-time-secs)
          s (e/server (double e/system-time-secs))]
      
      (println "s" (int s))
      (println "c" (int c))

      (dom/div (dom/text "client time: " c))
      (dom/div (dom/text "server time: " s))
      (dom/div (dom/text "difference: " (.toPrecision (- s c) 2))))))