(ns electric-fiddle.fiddle
  (:require clojure.string
            [contrib.electric-codemirror :refer [CodeMirror]]
            [electric-fiddle.api :as App]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            #?(:clj [electric-fiddle.read-src :refer [read-ns-src read-src]])
            [electric-fiddle.index :refer [Index]]))

(e/defn Fiddle [[directive alt-text target ?wrap :as route]]
  #_(assert (contains? #{"fiddle-ns fiddle"} directive))
  #_(dom/pre (dom/text (pr-str route)))
  (if (nil? (seq route)) (Index. []) ; todo varargs at user-main
    (let [target (symbol target)
          ?wrap (some-> ?wrap symbol)]
      (dom/div (dom/props {:class "user-examples"})
        (dom/fieldset
          (dom/props {:class "user-examples-code"})
          (dom/legend (dom/text "Code"))
          (CodeMirror. {:parent dom/node :readonly true} identity identity
            (e/server (case directive
                        "fiddle-ns" (read-ns-src target)
                        "fiddle" (read-src target)))))
        (dom/fieldset
          (dom/props {:class ["user-examples-target" (some-> target name)]})
          (dom/legend (dom/text "Result"))
          (if ?wrap
            (new (get App/pages ?wrap) [(get App/pages target)])
            (new (get App/pages target) [])))))))
