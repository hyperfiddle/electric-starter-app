(ns electric-fiddle.fiddle
  (:require clojure.string
            [contrib.electric-codemirror :refer [CodeMirror]]
            [electric-fiddle.api :as App]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.history :as history]
            #?(:clj [electric-fiddle.read-src :refer [read-ns-src read-src]])
            [electric-fiddle.index :refer [Index]]))

(e/defn Fiddle-impl [target ?wrap src]
  #_(dom/pre (dom/text target " " ?wrap " " src))
  (dom/div (dom/props {:class "user-examples"})
    (dom/fieldset
      (dom/props {:class "user-examples-code"})
      (dom/legend (dom/text "Code"))
      (CodeMirror. {:parent dom/node :readonly true} identity identity src))
    (dom/fieldset
      (dom/props {:class ["user-examples-target" (some-> target name)]})
      (dom/legend (dom/text "Result"))
      #_(dom/pre (dom/text (pr-str history/route)))
      #_(binding [history/build-route (fn [[page :as page-route] local-route]
                                        (println 'page-route page-route 'local-route local-route)
                                        `[~@(case page `Index nil page-route)
                                          ~@local-route])])
      (history/router nil
        (let [Target (get App/pages target)
              Wrap (when ?wrap (get App/pages ?wrap ::not-found))]
          (cond
            (= ::not-found Wrap) (dom/h1 (dom/text "not found, wrap: " ?wrap))
            (some? Wrap) (Wrap. [Target])
            () (Target. [])))))))

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
