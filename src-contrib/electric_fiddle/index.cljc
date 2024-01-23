(ns electric-fiddle.index
  (:require clojure.string
            [hyperfiddle :as hf]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.router :as r]))

(e/defn Index []
  (e/client
    (dom/h1 (dom/text `Index))
    (dom/pre (dom/text (pr-str r/route)))
    (e/for [[k _] (sort hf/pages)]
      (dom/div
        (r/link [(list k)] (dom/text (name k)))
        (dom/text " " k)))))
