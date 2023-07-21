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

(e/defn Fac [n]
  (let [Fac* (Y. (e/fn [Recur]
                   (e/fn [x]
                     (Trace.
                       (case x
                         0 1
                         (* x (Recur. (dec x))))))))]
    (Fac*. n)))

(e/defn Demo-Y-fib [] 
  (Fac. 19))