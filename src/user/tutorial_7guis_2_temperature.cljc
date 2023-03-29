(ns user.tutorial-7guis-2-temperature
  "see https://eugenkiss.github.io/7guis/tasks#temp"
  (:require
   clojure.math
   [hyperfiddle.electric :as e]
   [hyperfiddle.electric-dom2 :as dom]
   [hyperfiddle.electric-ui4 :as ui]
   [missionary.core :as m]))

(defn c->f [c] (+ (* c (/ 9 5)) 32))
(defn f->c [f] (* (- f 32) (/ 5 9)))
(defn random-value [_] (m/sp (m/? (m/sleep 1000)) (rand-int 40)))

(e/defn TemperatureConverter []
  (e/client
    (let [!t (atom 0), t (e/watch !t)]
      (reset! !t (new (e/task->cp (random-value t)))) ; test concurrent updates
      (dom/dl
        (dom/dt (dom/text "Celsius"))
        (dom/dd (ui/long (clojure.math/round t)
                  (e/fn [v] (reset! !t v))))
        (dom/dt (dom/text "Farenheit"))
        (dom/dd (ui/long (clojure.math/round (c->f t))
                  (e/fn [v] (reset! !t (f->c v)))))))))