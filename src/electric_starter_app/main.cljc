(ns electric-starter-app.main
  (:require [hyperfiddle.electric-de :as e :refer [$]]
            [hyperfiddle.electric-dom3-efns :as dom]
            [hyperfiddle.electric.impl.lang-de2 :as lang]
            [hyperfiddle.incseq :as i]
            #?(:cljs goog.dom)
            [missionary.core :as m]))

;; Saving this file will automatically recompile and update in your browser

#?(:clj (def db (atom [])))
#?(:clj (defn tx! [v] (swap! db conj v)))

#?(:cljs (defn read-on-enter [e]
           (prn :hi (-> e .-target .-value))
           (when (= "Enter" (.-key e))
             (let [v (-> e .-target .-value)]
               (set! (-> e .-target .-value) "")
               v))))

(e/defn TodoCreate []
  (e/client
    (dom/input
      (dom/props {:placeholder "buy milk"})
      (when-some [v (dom/listen "keydown" read-on-enter)]
        (e/server (tx! v))))))

(e/defn TodoItem [v]
  (e/client #_(prn :todo-item v) (dom/li (dom/text v))))

(e/defn Main [ring-request]
  (binding [dom/node js/document.body]
    (let [db (e/server (e/watch db))]
      (dom/div
        (dom/props {:class "todo-list"})
        ($ TodoCreate)
        (dom/ul
          (dom/props {:class "todo-items"})
          (e/server (e/cursor [v (e/diff-by identity db)] ($ TodoItem v))))
        (dom/p
          (dom/props {:class "counter"})
          (dom/span
            (dom/props {:class "count"})
            (dom/text (e/server (count db))))
          (dom/text " items left"))))))
