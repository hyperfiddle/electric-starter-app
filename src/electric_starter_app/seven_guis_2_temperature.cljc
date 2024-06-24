(ns electric-starter-app.seven-guis-2-temperature
  (:require [clojure.math]
            [hyperfiddle.electric-de :as e :refer [$]]
            [hyperfiddle.electric-dom3 :as dom]
            [missionary.core :as m]))

(defn c->f [c] (+ (* c (/ 9 5)) 32))
(defn f->c [f] (* (- f 32) (/ 5 9)))
(defn random-value [_] (m/sp (m/? (m/sleep 1000)) (rand-int 40)))

(e/defn TemperatureConverter []
  (let [!t (atom 0), t (e/watch !t)]
    (when-some [spend ($ e/CyclicToken true)]
      (spend (reset! !t ($ e/Task (random-value spend)))))
    (dom/dl
      (dom/dt (dom/text "Celsius"))
      (dom/dd (dom/input
                (dom/props {:type "number"})
                (when-not ($ dom/Focused?)
                  (set! (.-value dom/node) (clojure.math/round t)))
                ($ dom/On "input" (fn [e]
                                    (some->> (-> e .-target .-value parse-long) (reset! !t))))))
      (dom/dt (dom/text "Fahrenheit"))
      (dom/dd (dom/input
                (dom/props {:type "number"})
                (when-not ($ dom/Focused?)
                  (set! (.-value dom/node) (clojure.math/round (c->f t))))
                ($ dom/On "input" (fn [e]
                                    (some->> (-> e .-target .-value parse-long) f->c (reset! !t)))))))))
