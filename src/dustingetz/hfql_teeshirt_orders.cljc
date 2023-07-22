(ns dustingetz.hfql-teeshirt-orders
  (:require [clojure.spec.alpha :as s]
            #?(:clj [datascript.core :as d])
            #?(:clj [datascript.impl.entity :refer [entity?]])
            [hyperfiddle.api :as hf]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.hfql :refer [hfql]]
            [hyperfiddle.hfql-tree-grid :as hfql-tree-grid]
            [hyperfiddle.rcf :as rcf :refer [tests tap % with]]))

(declare conn schema nav orders)
(def ^:dynamic db)

(s/fdef orders :args (s/cat :needle string?) :ret (s/coll-of any?))
#?(:clj (defn orders [needle]
          (sort (d/q '[:find [?e ...] :in $ ?needle :where
                       [?e :order/email ?email]
                       [(clojure.string/includes? ?email ?needle)]]
                  db (or needle "")))))

(e/defn Teeshirt-orders [args]
  (e/client
    (dom/h1 (dom/text "hi"))
    (dom/pre (dom/text (pr-str (e/server (orders "")))))
    (dom/pre (dom/text (pr-str (e/server (str hf/db)))))
    (dom/pre (dom/text (pr-str (e/server (hfql [db hf/db] 42)))))
    (dom/pre (dom/text (pr-str (e/server (hfql [db hf/db] :db/id 1))))))
  
  (e/client
    (hfql-tree-grid/with-gridsheet-renderer
      (e/server (hfql [db hf/db] :db/id 1))
      (e/server
        (hfql [db hf/db] ; convey reactive db to clojure dynamic
          {(orders "")
           [:db/id]})))))

(e/defn HFQL-demo [args]
  (e/server
    (binding [hf/db (e/watch conn)
              hf/*schema* schema
              hf/*nav!*   nav]
      (Teeshirt-orders. args))))

#?(:clj (defn schema [db a] (get-in db [:schema a])))
#?(:clj (defn nav [db e a]
          (let [v (a (d/entity db e))]
            (if (entity? v) (or (:db/ident v) (:db/id v)) v))))

#?(:clj
   (defonce conn
     (doto (d/create-conn {})
       (d/transact! ; test data
         [{:order/email "alice@example.com" :order/gender :order/female}
          {:order/email "bob@example.com" :order/gender :order/male}
          {:order/email "charlie@example.com" :order/gender :order/male}]))))

#?(:clj
   (tests
     (alter-var-root #'db (constantly @conn))
     (some? db) := true
     (orders "") := [1 2 3]

     (with (e/run (tap (binding [hf/db db
                                 hf/*nav!* nav]
                         (hfql [] :db/id 1))))
       % := 1)

     (with (e/run (tap (binding [hf/db db
                                 hf/*nav!* nav]
                         (hfql []
                           {(orders "")
                            [:db/id
                             :order/email
                             :order/gender]}))))
       % := {`(dustingetz.hfql-teeshirt-orders/orders "")
             [{:db/id 1, :order/email "alice@example.com", :order/gender :order/female}
              {:db/id 2, :order/email "bob@example.com", :order/gender :order/male}
              {:db/id 3, :order/email "charlie@example.com", :order/gender :order/male}]})))