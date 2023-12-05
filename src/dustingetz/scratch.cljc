(ns dustingetz.scratch
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))


(e/defn Scratch []
  (e/client (dom/pre (dom/text "yo"))))
