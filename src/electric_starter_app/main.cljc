(ns electric-starter-app.main
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [taoensso.telemere :as t]))

;; Saving this file will automatically recompile and update in your browser

(e/defn Main [ring-request]
  (e/client
    (binding [dom/node js/document.body]
      (t/log! "testing!")
      (dom/h1 (dom/text "Hello from Electric Clojure"))
      (dom/p (dom/text "Source code for this page is in ")
             (dom/code (dom/text "src/electric_start_app/main.cljc")))
      (dom/p (dom/text "Make sure you check the ")
        (dom/a (dom/props {:href "https://electric.hyperfiddle.net/" :target "_blank"})
          (dom/text "Electric Tutorial"))))))
