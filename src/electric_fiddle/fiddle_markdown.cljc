(ns electric-fiddle.fiddle-markdown
  (:require clojure.string
            [electric-fiddle.fiddle :refer [Fiddle-fn]]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            #?(:clj [markdown.core :refer [md-to-html-string]])
            [hyperfiddle.rcf :refer [tests]]
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
  {'electric-y-combinator "src-fiddles/dustingetz/electric_y_combinator.md"
   'hfql-intro "src-fiddles/dustingetz/hfql_intro.md"
   'hfql-teeshirt-orders "src-fiddles/dustingetz/hfql_teeshirt_orders.md"

   'demo_two_clocks "src-fiddles/electric_tutorial/demo_two_clocks.md"})

(defn parse-md-directive [s]
  (let [[_ extension alt-text arg arg2] (re-find #"!(.*?)\[(.*?)\]\((.*?)\)(?:\((.*?)\))?" s)]
    [(symbol extension) alt-text arg arg2]))

(tests
  (parse-md-directive "!foo[example](https://example.com)")
  := ['foo "example" "https://example.com" nil]
  (parse-md-directive "!foo[example](b)(c)")
  := ['foo "example" "b" "c"])

(e/defn ExtensionNotFound [s & directive]
  (e/client (dom/div (dom/text "Unsupported markdown directive: " (pr-str directive)))))

(e/defn Custom-markdown [extensions essay-filename]
  (e/server
    (e/for [s (parse-sections (slurp essay-filename))]
      (e/client
        (if (.startsWith s "!")
          (let [[extension & args] (parse-md-directive s)]
            (if-let [F (get extensions extension)]
              (e/apply F args)
              (ExtensionNotFound. s)))
          (dom/div (dom/props {:class "markdown-body user-examples-readme"})
            (e/server (Markdown. s))))))))
