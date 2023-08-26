(ns electric-fiddle.essay
  (:require clojure.string
            [electric-fiddle.fiddle :refer [Fiddle-embed]]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.history :as history]
            #?(:clj [markdown.core :refer [md-to-html-string]])
            [hyperfiddle.rcf :refer [tests]]
            [electric-fiddle.api :as App]
            electric-fiddle.index))

#?(:clj (defn parse-sections [md-str]
          (->> md-str clojure.string/split-lines
            (partition-by #(not= \! (first %))) ; isolate the directive lines
            (map #(apply str (interpose "\n" %))))))

(comment (parse-sections (slurp (essays 'electric-y-combinator))))

(e/defn Markdown [?md-str]
  (e/client
    (let [html (e/server (some-> ?md-str md-to-html-string))]
      (set! (.-innerHTML dom/node) html))))

(def essays 
  {'electric-y-combinator "src/dustingetz/electric_y_combinator.md"
   'hfql-intro "src/dustingetz/hfql_intro.md"
   'hfql-teeshirt-orders "src/dustingetz/hfql_teeshirt_orders.md"})

(defn parse-md-directive [s]
  (let [[_ a b c d] (re-find #"!(.*?)\[(.*?)\]\((.*?)\)(?:\((.*?)\))?" s)]
    [a b c d]))

(tests
  (parse-md-directive "!foo[example](https://example.com)")
  := ["foo" "example" "https://example.com" nil]
  (parse-md-directive "!foo[example](b)(c)")
  := ["foo" "example" "b" "c"])

(e/defn Essay [& [?essay]]
  #_(e/client (dom/div #_(dom/props {:class ""}))) ; fix css grid next
  (let [essay-filename (get essays ?essay)]
    (cond
      (nil? ?essay) (binding [App/pages essays] (electric-fiddle.index/Index.))
      (nil? essay-filename) (dom/h1 (dom/text "essay not found: " history/route))
      () (e/server
           (e/for [s (parse-sections (slurp essay-filename))]
             (e/client
               (if (.startsWith s "!")
                 (e/apply Fiddle-embed (parse-md-directive s))
                 (dom/div (dom/props {:class "markdown-body user-examples-readme"})
                   (e/server (Markdown. s))))))))))
