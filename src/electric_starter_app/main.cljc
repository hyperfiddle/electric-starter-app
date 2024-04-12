(ns electric-starter-app.main
  (:require [hyperfiddle.electric-de :as e :refer [$]]
            [hyperfiddle.electric-dom3 :as dom]
            [hyperfiddle.electric.impl.lang-de2 :as lang]
            [missionary.core :as m]))

;; Saving this file will automatically recompile and update in your browser

(e/defn Main [ring-request]
  (binding [dom/node js/document.body]
    (dom/h1 (dom/text (e/server "hello")))))

#_(e/defn ^::lang/print-cljs-source Main [ring-request]
  (binding [dom/node js/document.body]
    (dom/h1
      (dom/text "Hello from Electric Clojure"))
    #_(dom/p (dom/text "Source code for this page is in ")
        (dom/code (dom/text "src/electric_start_app/main.cljc")))
    #_(dom/p (dom/text "Make sure you check the ")
        (dom/a (dom/props {:href "https://electric.hyperfiddle.net/" :target "_blank"})
          (dom/text "Electric Tutorial")))))
