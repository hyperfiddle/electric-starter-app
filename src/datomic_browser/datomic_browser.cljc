(ns datomic-browser.datomic-browser
  (:require clojure.edn
            contrib.ednish
            [contrib.str :refer [any-matches?]]
            [contrib.data :refer [unqualify treelister]]
            #?(:clj [contrib.datomic-contrib :as dx])
            [contrib.datomic-m #?(:clj :as :cljs :as-alias) d]
            [contrib.gridsheet :as gridsheet :refer [Explorer]]
            [datomic-browser.domain :as D :refer [db conn schema]]
            #?(:clj datomic.api)
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.history :as history]
            [missionary.core :as m]))

(comment (ns-unmap *ns* 'model))

(e/defn RecentTx []
  (e/client (dom/h1 (dom/text "Recent Txs")))
  (Explorer.
    (treelister (new (->> (d/datoms> db {:index :aevt, :components [:db/txInstant]})
                       (m/reductions conj ())
                       (m/relieve {})))
      (fn [_]) any-matches?)
    {::gridsheet/page-size 30
     ::gridsheet/row-height 24
     ::gridsheet/columns [:db/id :db/txInstant]
     ::gridsheet/grid-template-columns "10em auto"
     ::gridsheet/Format
     (e/fn [[e _ v tx op :as record] a]
       (case a
         :db/id (e/client (history/link [::tx tx] (dom/text tx)))
         :db/txInstant (e/client (dom/text (pr-str v))) #_(e/client (.toLocaleDateString v))))}))

(e/defn Attributes []
  (e/client (dom/h1 (dom/text "Attributes")))
  (let [cols [:db/ident :db/valueType :db/cardinality :db/unique :db/isComponent
              #_#_#_#_:db/fulltext :db/tupleType :db/tupleTypes :db/tupleAttrs]]
    (Explorer.
      (treelister (->> (dx/attributes> db cols)
                    (m/reductions conj [])
                    (m/relieve {})
                    new
                    (sort-by :db/ident)) ; sort by db/ident which isn't available
        (fn [_]) any-matches?)
      {::gridsheet/page-size 15
       ::gridsheet/row-height 24
       ::gridsheet/columns cols
       ::gridsheet/grid-template-columns "auto 6em 4em 4em 4em"
       ::gridsheet/Format
       (e/fn [row col]
         (e/client
           (let [v (col row)]
             (case col
               :db/ident (history/link [::attribute v] (dom/text v))
               :db/valueType (some-> v :db/ident name dom/text)
               :db/cardinality (some-> v :db/ident name dom/text)
               :db/unique (some-> v :db/ident name dom/text)
               (dom/text (str v))))))})))

(e/defn Format-entity [[k v :as row] col]
  (assert (some? schema))
  (case col
    ::k (cond
          (= :db/id k) (e/client (dom/text k)) ; :db/id is our schema extension, can't nav to it
          (contains? schema k) (e/client (history/link [::attribute k] (dom/text k)))
          () (e/client (dom/text (str k)))) ; str is needed for Long db/id, why?
    ::v (if-not (coll? v) ; don't render card :many intermediate row
          (let [[valueType cardinality]
                ((juxt (comp unqualify dx/identify :db/valueType)
                   (comp unqualify dx/identify :db/cardinality)) (k schema))]
            (cond
              (= :db/id k) (e/client (history/link [::entity v] (dom/text v)))
              (= :ref valueType) (e/client (history/link [::entity v] (dom/text v)))
              () (e/client (dom/text (pr-str v))))))))

(e/defn EntityDetail [e]
  (assert e)
  (e/client (dom/h1 (dom/text "Entity detail: " e))) ; treeview on the entity
  (Explorer.
    ;; TODO inject sort
    (treelister (new (e/task->cp (d/pull db {:eid e :selector ['*] :compare compare})))
      (partial dx/entity-tree-entry-children schema)
      any-matches?)
    {::gridsheet/page-size 15
     ::gridsheet/row-height 24
     ::gridsheet/columns [::k ::v]
     ::gridsheet/grid-template-columns "15em auto"
     ::gridsheet/Format Format-entity}))

(e/defn EntityHistory [e]
  (assert e)
  (e/client (dom/h1 (dom/text "Entity history: " (pr-str e))))
  (Explorer.
    ; accumulate what we've seen so far, for pagination. Gets a running count. Bad?
    (treelister (new (->> (dx/entity-history-datoms> db e)
                       (m/reductions conj []) ; track a running count as well?
                       (m/relieve {})))
      (fn [_]) any-matches?)
    {::gridsheet/page-size 20
     ::gridsheet/row-height 24
     ::gridsheet/columns [::e ::a ::op ::v ::tx-instant ::tx]
     ::gridsheet/grid-template-columns "10em 10em 3em auto auto 9em"
     ::gridsheet/Format
     (e/fn [[e aa v tx op :as row] a]
       (when row ; when this view unmounts, somehow this fires as nil
         (case a
           ::op (e/client (dom/text (name (case op true :db/add false :db/retract))))
           ::e (e/client (history/link [::entity e] (dom/text e)))
           ::a (if (some? aa)
                 (let [ident (:db/ident (new (e/task->cp (d/pull db {:eid aa :selector [:db/ident]}))))]
                   (e/client (dom/text (pr-str ident)))))
           ::v (e/client (some-> v pr-str dom/text))
           ::tx (e/client (history/link [::tx tx] (dom/text tx)))
           ::tx-instant (let [x (:db/txInstant (new (e/task->cp (d/pull db {:eid tx :selector [:db/txInstant]}))))]
                          (e/client (pr-str (dom/text x))))
           (str v))))}))

(e/defn AttributeDetail [a]
  (e/client (dom/h1 (dom/text "Attribute detail: " a)))
  (Explorer.
    (treelister (new (->> (d/datoms> db {:index :aevt, :components [a]})
                       (m/reductions conj [])
                       (m/relieve {})))
      (fn [_]) any-matches?)
    {::gridsheet/page-size 20
     ::gridsheet/row-height 24
     ::gridsheet/columns [:e :a :v :tx]
     ::gridsheet/grid-template-columns "15em 15em calc(100% - 15em - 15em - 9em) 9em"
     ::gridsheet/Format
     (e/fn [[e _ v tx op :as x] k]
       (e/client
         (case k
           :e (history/link [::entity e] (dom/text e))
           :a (dom/text (pr-str a)) #_(let [aa (new (e/task->cp (dx/ident! db aa)))] aa)
           :v (some-> v str dom/text) ; todo when a is ref, render link
           :tx (history/link [::tx tx] (dom/text tx)))))}))

(e/defn TxDetail [e]
  (e/client (dom/h1 (dom/text "Tx detail: " e)))
  (Explorer.
    (treelister (new (->> (d/tx-range> conn {:start e, :end (inc e)}) ; global
                       (m/eduction (map :data) cat)
                       (m/reductions conj [])
                       (m/relieve {})))
      (fn [_]) any-matches?)
    {::gridsheet/page-size 20
     ::gridsheet/row-height 24
     ::gridsheet/columns [:e :a :v :tx]
     ::gridsheet/grid-template-columns "15em 15em calc(100% - 15em - 15em - 9em) 9em"
     ::gridsheet/Format
     (e/fn [[e aa v tx op :as x] a]
       (case a
         :e (let [e (new (e/task->cp (dx/ident! db e)))] (e/client (history/link [::entity e] (dom/text e))))
         :a (let [aa (new (e/task->cp (dx/ident! db aa)))] (e/client (history/link [::attribute aa] (dom/text aa))))
         :v (pr-str v) ; when a is ref, render link
         (str tx)))}))

(e/defn DbStats []
  (e/client (dom/h1 (dom/text "Db stats")))
  (Explorer.
    (treelister
      (new (e/task->cp (d/db-stats db)))
      (fn [[k v]] (condp = k :attrs (into (sorted-map) v) nil))
      any-matches?)
    {::gridsheet/page-size 20
     ::gridsheet/row-height 24
     ::gridsheet/columns [::k ::v]
     ::gridsheet/grid-template-columns "20em auto"
     ::gridsheet/Format
     (e/fn [[k v :as row] col]
       (e/client
         (case col
           ::k (dom/text (pr-str k))
           ::v (cond
                 (= k :attrs) nil ; print children instead
                 () (dom/text (pr-str v))))))})) ; {:count 123}

(comment
  {:datoms 800958,
   :attrs
   {:release/script {:count 11435},
    :label/type {:count 870}
    ... ...}})

(e/defn DatomicBrowser [& [page x]]
  (dom/h1 (dom/text "Datomic browser"))
  (dom/link (dom/props {:rel :stylesheet, :href "gridsheet-optional.css"}))
  (dom/div (dom/props {:class "user-gridsheet-demo"})
    (dom/div (dom/text "Nav: ")
      (history/link [::summary] (dom/text "home")) (dom/text " ")
      (history/link [::db-stats] (dom/text "db-stats")) (dom/text " ")
      (history/link [::recent-tx] (dom/text "recent-tx")))
    (case (or page ::summary)
      ::summary (history/router 1 (e/server (Attributes.)))
      ::attribute (history/router 2 (e/server (AttributeDetail. x)))
      ::tx (history/router 2 (e/server (TxDetail. x)))
      ::entity (do (history/router 2
                     (history/router ::entity-detail (e/server (EntityDetail. x)))
                     (history/router ::entity-history (e/server (EntityHistory. x)))))
      ::db-stats (history/router 1 (e/server (DbStats.)))
      ::recent-tx (history/router 1 (e/server (RecentTx.)))
      (e/client (dom/text "no matching route: " (pr-str page))))))
