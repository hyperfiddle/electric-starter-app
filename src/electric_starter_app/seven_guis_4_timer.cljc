(ns electric-starter-app.seven-guis-4-timer
  (:require
   [hyperfiddle.electric-de :as e :refer [$]]
   [hyperfiddle.electric-dom3 :as dom]))

(def initial-goal 10) ; seconds
(defn seconds [milliseconds] (/ (Math/floor (/ milliseconds 100)) 10))
(defn second-precision [milliseconds]
  (-> milliseconds (/ 1000) (Math/floor) (* 1000))) ; drop milliseconds

(defn now [] #?(:cljs (second-precision (js/Date.now))))

(e/defn Timer []
  (let [!goal (atom initial-goal), goal (e/watch !goal), goal-ms (* 1000 goal)
        !start (atom (now)), start (e/watch !start)
        time (min goal-ms (- (second-precision ($ e/SystemTimeMs)) start))]
    (dom/div
      (dom/props {:style {:display "grid", :width "20em", :grid-gap "0 1rem", :align-items "center"}})
      (dom/span (dom/text "Elapsed time:"))
      (dom/progress (dom/props {:max goal-ms, :value time, :style {:grid-column 2}}))
      (dom/span (dom/text (seconds time) " s"))
      (dom/span (dom/props {:style {:grid-row 3}}) (dom/text "Duration: " goal "s"))
      (dom/input
        (dom/props {:type "range", :min 0, :max 60, :style {:grid-row 3}})
        ($ dom/On "input" (fn [e] (->> e .-target .-value parse-long (reset! !goal)))))
      (dom/button
        (dom/text "Reset")
        (dom/props {:style {:grid-row 4, :grid-column "1/3"}})
        ($ dom/On "click" (fn [_] (reset! !start (now))))))))
