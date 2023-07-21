(ns dustingetz.essay
  (:require clojure.string
            [contrib.electric-codemirror :refer [CodeMirror]]
            [contrib.stuff :refer [Markdown]]
            [electric-fiddle.api :as App]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.history :as history]))

#?(:clj (defn parse-sections [md-str]
          (->> md-str clojure.string/split-lines
            (partition-by #(not= \! (first %))) ; isolate the directive lines
            (map #(apply str (interpose "\n" %))))))

(def essays {'electric-y-combinator "src/dustingetz/electric_y_combinator.md"})

(comment (parse-sections (slurp (essays "electric-y-combinator")))
  (contrib.stuff/get-src 'dustingetz.y-fib/Demo-Y-fib)
  (contrib.stuff/get-src 'dustingetz.y-dir/Demo-Y-dir)
  )

(e/defn Fiddle [page]
  (dom/div (dom/props {:class "user-examples"})
    (dom/fieldset
      (dom/props {:class "user-examples-code"})
      (dom/legend (dom/text "Code"))
      (CodeMirror. {:parent dom/node :readonly true} identity identity (e/server (contrib.stuff/get-src page))))
    (dom/fieldset
      (dom/props {:class ["user-examples-target" (some-> page name)]})
      (dom/legend (dom/text "Result"))
      (new (get App/pages page)))))

(e/defn Index [essays]
  (e/client
    (dom/h1 (dom/text `Index))
    (e/for [[k _] essays]
      (dom/div (history/link [k] (dom/text (name k)))))))

(e/defn Essay []
  #_(e/client (dom/div #_(dom/props {:class ""}))) ; fix css grid next 
  (let [filename (get essays history/route)]
    (cond
      (nil? history/route) (Index. essays)
      (nil? filename) (dom/h1 (dom/text "essay not found: " history/route))
      () (e/server
           (e/for [run (parse-sections (slurp filename))]
             (e/client
               (if (= \! (first run))
                 (Fiddle. (symbol (second (re-find #"!embed\[]\(([^\)]+)" run))))
                 (dom/div (dom/props {:class "markdown-body user-examples-readme"})
                   (e/server (Markdown. run))))))))))
