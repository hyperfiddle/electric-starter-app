(ns electric-starter-app.seven-guis-1-counter
  (:require
   [hyperfiddle.electric-de :as e :refer [$]]
   [hyperfiddle.electric-dom3 :as dom]))

(e/defn Counter []
  (let [!state (atom 0)]
    (dom/p (dom/text (e/watch !state)))
    (dom/button
      (dom/text "Count")
      ($ dom/On "click" (fn [_] (swap! !state inc))))))
