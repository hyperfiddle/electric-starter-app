(ns hello-fiddle.hello-fiddle
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

(e/defn Hello []
  (dom/h1 (dom/text "Hello world")))