(ns dustingetz.essay
  (:require clojure.string
            [electric-fiddle.fiddle :refer [Fiddle]]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.history :as history]))

#?(:clj (defn parse-sections [md-str]
          (->> md-str clojure.string/split-lines
            (partition-by #(not= \! (first %))) ; isolate the directive lines
            (map #(apply str (interpose "\n" %))))))

(comment (parse-sections (slurp (essays "electric-y-combinator"))))

(e/defn Markdown [?md-str]
  (e/client
    (let [html (e/server (some-> ?md-str markdown.core/md-to-html-string))]
      (set! (.-innerHTML dom/node) html))))

(def essays {'electric-y-combinator "src/dustingetz/electric_y_combinator.md"})

(e/defn Index [essays]
  (e/client
    (dom/h1 (dom/text `Index))
    (e/for [[k _] essays]
      (dom/div (history/link [k] (dom/text (name k)))))))

(defn parse-fiddle-name-from-md-directive [s]
  (symbol (second (re-find #"!fiddle\[]\(([^\)]+)" s))))

(e/defn Essay []
  #_(e/client (dom/div #_(dom/props {:class ""}))) ; fix css grid next 
  (let [filename (get essays history/route)]
    (cond
      (nil? history/route) (Index. essays)
      (nil? filename) (dom/h1 (dom/text "essay not found: " history/route))
      () (e/server
           (e/for [s (parse-sections (slurp filename))]
             (e/client
               (if (= \! (first s))
                 (Fiddle. (parse-fiddle-name-from-md-directive s))
                 (dom/div (dom/props {:class "markdown-body user-examples-readme"})
                   (e/server (Markdown. s))))))))))
