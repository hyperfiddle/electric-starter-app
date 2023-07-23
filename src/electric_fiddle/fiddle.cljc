(ns electric-fiddle.fiddle
  (:require clojure.string
            [contrib.electric-codemirror :refer [CodeMirror]]
            [electric-fiddle.api :as App]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            #?(:clj [electric-fiddle.read-src :refer [read-ns-src read-src]])
            [electric-fiddle.index :refer [Index]]))

(e/defn Fiddle-impl [target ?wrap src]
  (dom/div (dom/props {:class "user-examples"})
    (dom/fieldset
      (dom/props {:class "user-examples-code"})
      (dom/legend (dom/text "Code"))
      (CodeMirror. {:parent dom/node :readonly true} identity identity src))
    (dom/fieldset
      (dom/props {:class ["user-examples-target" (some-> target name)]})
      (dom/legend (dom/text "Result"))
      (if ?wrap
        (new (get App/pages ?wrap) [(get App/pages target)])
        (new (get App/pages target) [])))))

(e/defn Fiddle [[target-s ?wrap :as route]] ; direct fiddle link
  #_(dom/pre (dom/text (pr-str route)))
  (if (nil? (seq route)) (Index. []) ; todo varargs at user-main 
    (Fiddle-impl. target-s (some-> ?wrap symbol)
      (e/server (read-ns-src (symbol target-s))))))

(e/defn Fiddle-embed [[directive alt-text target-s ?wrap :as route]]
  (assert (contains? #{"fiddle-ns" "fiddle"} directive) directive)
  #_(dom/pre (dom/text (pr-str route)))
  (let [target (symbol target-s)
        ?wrap (some-> ?wrap symbol)]
    (Fiddle-impl. target ?wrap
      (e/server (case directive
                  "fiddle-ns" (read-ns-src target)
                  "fiddle" (read-src target))))))
