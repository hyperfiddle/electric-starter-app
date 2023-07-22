(ns dustingetz.essay
  (:require clojure.string
            [electric-fiddle.fiddle :refer [Fiddle Fiddle-ns]]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.history :as history]
            #?(:clj [markdown.core :refer [md-to-html-string]])))

#?(:clj (defn parse-sections [md-str]
          (->> md-str clojure.string/split-lines
            (partition-by #(not= \! (first %))) ; isolate the directive lines
            (map #(apply str (interpose "\n" %))))))

(comment (parse-sections (slurp (essays "electric-y-combinator"))))

(e/defn Markdown [?md-str]
  (e/client
    (let [html (e/server (some-> ?md-str md-to-html-string))]
      (set! (.-innerHTML dom/node) html))))

(def essays {'electric-y-combinator "src/dustingetz/electric_y_combinator.md"})

(e/defn Index [essays]
  (e/client
    (dom/h1 (dom/text `Index))
    (e/for [[k _] essays]
      (let [href (vec (concat history/route [k]))]
        (dom/div (history/link href 
                   (dom/text (name k))
                   #_(dom/text " " (history/build-route history/history href))))))))

(defn parse-fiddle-ns-directive [s] (second (re-find #"!fiddle-ns\[]\(([^\)]+)" s)))
(defn parse-fiddle-directive [s] (second (re-find #"!fiddle\[]\(([^\)]+)" s)))

(e/defn Essay [[essay]]
  #_(e/client (dom/div #_(dom/props {:class ""}))) ; fix css grid next
  (let [essay-filename (get essays essay)]
    (cond
      (nil? essay) (Index. essays)
      (nil? essay-filename) (dom/h1 (dom/text "essay not found: " history/route))
      () (e/server
           (e/for [s (parse-sections (slurp essay-filename))]
             (e/client
               (cond
                 (.startsWith s "!fiddle-ns") (Fiddle-ns. [(symbol (parse-fiddle-ns-directive s))])
                 (.startsWith s "!fiddle") (Fiddle. [(symbol (parse-fiddle-directive s))])
                 () (dom/div (dom/props {:class "markdown-body user-examples-readme"})
                      (e/server (Markdown. s))))))))))
