(ns electric-starter-app.seven-guis-2-temperature
  (:require [clojure.math]
            [contrib.trace-de :as ct]
            [hyperfiddle.electric-de :as e :refer [$]]
            [hyperfiddle.electric-dom3 :as dom]
            [missionary.core :as m]))

(defn c->f [c] (+ (* c (/ 9 5)) 32))
(defn f->c [f] (* (- f 32) (/ 5 9)))
(defn set-random-valueT [!t]
  (m/sp (loop []
          (m/? (m/sleep 1000))
          (reset! !t (rand-int 40))
          (recur))))

(e/defn TemperatureConverter []
  (ct/with-defaults
    (let [!t (atom 0), t (e/watch !t)]
      (ct/trace ::random ($ e/Task (set-random-valueT !t)))
      #_(when-some [spend! ($ e/CyclicToken true)]
        (spend! (reset! !t (ct/trace ::random ct/->stable-trace-id identity
                             ($ e/Task (m/sleep 1000 (rand-int 40)))))))
      (dom/dl
        (dom/dt (dom/text "Celsius"))
        (ct/trace ::celsius
          (dom/dd (dom/input
                    (dom/props {:type "number"})
                    (when-not (ct/trace ::focused? ($ dom/Focused?))
                      (set! (.-value dom/node) (clojure.math/round t)))
                    ($ dom/On "input" (fn [e]
                                        (some->> (-> e .-target .-value parse-long) (reset! !t)))))))
        (dom/dt (dom/text "Fahrenheit"))
        (ct/trace ::fahrenheit
          (dom/dd (dom/input
                    (dom/props {:type "number"})
                    (when-not (ct/trace ::focused? ($ dom/Focused?))
                      (set! (.-value dom/node) (clojure.math/round (c->f t))))
                    ($ dom/On "input" (fn [e]
                                        (some->> (-> e .-target .-value parse-long) f->c (reset! !t)))))))))
    ($ ct/TraceView)))
