(ns electric-fiddle.fiddle
  (:require clojure.string
            [contrib.electric-codemirror :refer [CodeMirror]]
            [electric-fiddle.api :as App]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.history :as history]))

#?(:clj (defn resolve-var-or-ns [sym]
          (if (qualified-symbol? sym)
            (ns-resolve *ns* sym)
            (find-ns sym))))

#?(:clj (defn get-src [sym]
          (try (-> (resolve-var-or-ns sym) meta :file
                 (->> (str "src/")) slurp)
            (catch java.io.FileNotFoundException _))))

(comment
  (get-src 'dustingetz.y-fib/Y-fib)
  (get-src 'dustingetz.y-dir/Y-dir))

(e/defn Index [] ; duplicated from user-main/Index to avoid cycle in Electric compiler
  (e/client
    (dom/h1 (dom/text `Index))
    #_(binding [history/build-route (fn [top-route paths'] (vec (concat (butlast top-route) paths')))])
    (e/for [[k _] App/pages]
      (let [href (vec (concat history/route [k]))]
        (dom/div (history/link href
                   (dom/text (name k))
                   #_(dom/text " " (history/build-route history/history href))))))))

(e/defn Fiddle [[fiddle & args :as route]]
  #_(dom/pre (dom/text (pr-str route)))
  (cond
    (nil? fiddle) (Index.)
    () (dom/div (dom/props {:class "user-examples"})
         (dom/fieldset
           (dom/props {:class "user-examples-code"})
           (dom/legend (dom/text "Code"))
           (CodeMirror. {:parent dom/node :readonly true} identity identity (e/server (get-src fiddle))))
         (dom/fieldset
           (dom/props {:class ["user-examples-target" (some-> fiddle name)]})
           (dom/legend (dom/text "Result"))
           (new (get App/pages fiddle) args)))))

#_
#?(:clj (defn get-companion-md [sym]
          (try (some-> sym ; nil if userland typo
                 resolve-var-or-ns meta :file
                 (clojure.string/split #"\.(clj|cljs|cljc)") first (str ".md")
                 (->> (str "src/")) slurp)
            (catch java.io.FileNotFoundException _))))

(comment
  (get-src `user.demo-two-clocks/TwoClocks)
  (get-src 'user)
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