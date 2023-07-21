(ns contrib.stuff
  (:require clojure.string
            #?(:clj markdown.core)
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

#?(:clj (defn resolve-var-or-ns [sym]
          (if (qualified-symbol? sym)
            (ns-resolve *ns* sym)
            (find-ns sym))))

#?(:clj (defn get-src [sym]
          (try (-> (resolve-var-or-ns sym) meta :file
                 (->> (str "src/")) slurp)
            (catch java.io.FileNotFoundException _))))

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
  (resolve-var-or-ns 'user.electric-y-combinator)
  
  )

(e/defn Markdown [?md-str]
  (e/client
    (let [html (e/server (some-> ?md-str markdown.core/md-to-html-string))]
      (set! (.-innerHTML dom/node) html))))
