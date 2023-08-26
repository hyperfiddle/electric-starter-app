(ns dustingetz.y-fib
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

(e/defn Y [Gen]
  (new (e/fn [F] (F. F))            ; call-with-self
    (e/fn F [F]
      (Gen. (e/fn Recur [x]
              (new (F. F) x))))))

(e/defn Trace [x]
  (dom/div (dom/text x))
  x)

(e/defn Fib [Recur]
  (e/fn [x]
    (Trace.
      (case x
        0 1
        (* x (Recur. (dec x)))))))

(e/defn Y-fib []
  (new (Y. Fib) 15))