(ns hello-fiddle.hello-fiddle
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

(e/defn Hello []
  (e/client
    (dom/h1 (dom/text "Hello world"))))
