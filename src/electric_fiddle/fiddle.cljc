(ns electric-fiddle.fiddle
  (:require clojure.string
            [contrib.electric-codemirror :refer [CodeMirror]]
            [electric-fiddle.api :as App]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            #?(:clj [electric-fiddle.read-src :refer [read-ns-src read-src]])
            [hyperfiddle.history :as history]))

(e/defn Index [] ; duplicated from user-main/Index to avoid cycle in Electric compiler
  (e/client
    (dom/h1 (dom/text `Index))
    #_(binding [history/build-route (fn [top-route paths'] (vec (concat (butlast top-route) paths')))])
    (e/for [[k _] App/pages]
      (let [href (vec (concat history/route [k]))]
        (dom/div (history/link href
                   (dom/text (name k))
                   #_(dom/text " " (history/build-route history/history href))))))))

(e/defn Fiddle [[directive alt-text target ?wrap :as route]]
  #_(assert (contains? #{"fiddle-ns fiddle"} directive))
  #_(dom/pre (dom/text (pr-str route)))
  (if (nil? (seq route)) (Index.) ; todo varargs at user-main
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

#_
#?(:clj (defn get-companion-md [sym]
          (try (some-> sym ; nil if userland typo
                 resolve-var-or-ns meta :file
                 (clojure.string/split #"\.(clj|cljs|cljc)") first (str ".md")
                 (->> (str "src/")) slurp)
            (catch java.io.FileNotFoundException _))))

(comment
  (get-ns-src `user.demo-two-clocks/TwoClocks)
  (get-ns-src 'user)
  (get-companion-md 'user)
  (-> (resolve-var-or-ns 'user) meta :file)
  (get-companion-md `user.demo-two-clocks/TwoClocks)
  (resolve-var-or-ns `user.demo-two-clocks/TwoClocks)
  (resolve-var-or-ns `user.demo-two-clocks)
  (qualified-symbol? `user.demo-two-clocks/TwoClocks)
  (ns-resolve *ns* `user.demo-two-clocks/TwoClocks)
  (the-ns 'user.demo-two-clocks)
  (find-ns 'user.demo-two-clocks)
  (find-ns 'contrib.stuff)
  (find-ns 'user)
  (find-ns 'user.electric-y-combinator)

  (get-companion-md `dustingetz.electric-y-combinator)
  (get-companion-md 'user.electric-y-combinator/Page)
  (resolve-var-or-ns `user.electric-y-combinator/Page)
  (resolve-var-or-ns 'user.electric-y-combinator))