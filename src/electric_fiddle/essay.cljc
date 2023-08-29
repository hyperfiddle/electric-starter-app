(ns electric-fiddle.essay
  (:require clojure.string
            [electric-fiddle.config :as config]
            [electric-fiddle.fiddle :refer [Fiddle-fn Fiddle-ns]]
            [electric-fiddle.fiddle-markdown :refer [Custom-markdown]]
            [electric-fiddle.index :refer [Index]]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.history :as history]))

(def essays
  {'electric-y-combinator "src/dustingetz/electric_y_combinator.md"
   'hfql-intro "src/dustingetz/hfql_intro.md"
   'hfql-teeshirt-orders "src/dustingetz/hfql_teeshirt_orders.md"})

(e/def extensions 
  {'fiddle Fiddle-fn
   'fiddle-ns Fiddle-ns})

(e/defn Essay [& [?essay]]
  #_(e/client (dom/div #_(dom/props {:class ""}))) ; fix css grid next
  (let [essay-filename (get essays ?essay)]
    (cond
      (nil? ?essay) (binding [config/pages essays] (Index.))
      (nil? essay-filename) (dom/h1 (dom/text "Essay not found: " history/route))
      () (Custom-markdown. extensions essay-filename))))
