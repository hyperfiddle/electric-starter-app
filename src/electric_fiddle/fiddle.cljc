(ns electric-fiddle.fiddle
  (:require clojure.string
            [contrib.electric-codemirror :refer [CodeMirror]]
            [electric-fiddle.config :as config]
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
        (let [Target (get config/pages target)
              Wrap (when ?wrap (get config/pages ?wrap ::not-found))]
          (cond
            (= ::not-found Wrap) (dom/h1 (dom/text "not found, wrap: " ?wrap))
            (some? Wrap) (Wrap. Target)
            () (Target.)))))))

(e/defn Fiddle-fn [& [alt-text target-s ?wrap :as args]]
  (let [target (symbol target-s)]
    (Fiddle-impl. target (some-> ?wrap symbol)
      (e/server (read-src target)))))

(e/defn Fiddle-ns [& [alt-text target-s ?wrap :as args]]
  (let [target (symbol target-s)]
    (Fiddle-impl. target (some-> ?wrap symbol)
      (e/server (read-ns-src target)))))

(e/defn Fiddle [& [target-s ?wrap :as route]] ; direct fiddle link http://localhost:8080/electric-fiddle.fiddle!Fiddle/dustingetz.y-fib!Y-fib
  (if (nil? (seq route)) (Index.)
    (Fiddle-ns. "" target-s ?wrap)))