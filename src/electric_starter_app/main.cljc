(ns electric-starter-app.main
  (:require [hyperfiddle.electric-de :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [missionary.core :as m]))

;; Saving this file will automatically recompile and update in your browser

(e/defn Main [ring-request]
  (binding [dom/node js/document.body]
    (e/input (m/observe (fn [!] (prn :hi) (! nil) #(prn :bye))))
    (dom/h1 (dom/text "Hello from Electric Clojure"))
    (dom/p (dom/text "Source code for this page is in ")
        (dom/code (dom/text "src/electric_start_app/main.cljc")))
    (dom/p (dom/text "Make sure you check the ")
        (dom/a (dom/props {:href "https://electric.hyperfiddle.net/" :target "_blank"})
          (dom/text "Electric Tutorial")))))
