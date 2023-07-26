(ns dustingetz.hfql-teeshirt-orders
  (:require [clojure.spec.alpha :as s]
            [contrib.electric-codemirror :refer [CodeMirror]]
            contrib.str
            #?(:clj [datomic.api :as d])
            [electric-fiddle.index :refer [Index]]
            [hyperfiddle.api :as hf]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.hfql :refer [hfql]]
            [hyperfiddle.hfql-tree-grid :as hfql-tree-grid]
            [hyperfiddle.rcf :as rcf :refer [tests tap % with]]
            #?(:clj [user.orders-datomic :refer [orders genders shirt-sizes]]) ; from vendor/hfql
            ))

(e/defn Codemirror-edn [x]
  (CodeMirror. {:parent dom/node :readonly true} identity contrib.str/pprint-str 
    x))

(e/defn Teeshirt-orders [args]
  (Codemirror-edn.
    (e/server
      (hfql [hf/*$* hf/db]
        {(orders "")
         [:db/id
          :order/email]}))))

(e/defn Teeshirt-orders-2 [args]
  (hfql-tree-grid/with-gridsheet-renderer
    (e/server
      (hfql [hf/*$* hf/db]
        {(orders .)
         [:db/id
          :order/email]}))))

(e/defn Teeshirt-orders-3 [args]
  (hfql-tree-grid/with-gridsheet-renderer
    (e/server
      (hfql [hf/*$* hf/db]
        {(orders .)
         [:db/id
          :order/email
          :order/gender
          :order/shirt-size]}))))

(e/defn Teeshirt-orders-4 [_]
  #_
  (e/client
    (dom/table
      (e/server
        (e/for-by :db/id [record (orders "")]
          (let [{:keys [db/id
                        order/email
                        order/gender
                        order/shirt-size]} record]
            (e/client
              (dom/tr
                (dom/td (dom/input id))
                (dom/td (dom/input email))
                (dom/td (dom/select
                          :value gender
                          :options (e/fn [filter]
                                     (e/server (genders filter)))))
                (dom/td (dom/select
                          :value shirt-size
                          :options (e/fn [filter]
                                     (e/server (shirt-sizes gender filter)))))))))))))

(e/defn Teeshirt-orders-5 [args]
  (hfql-tree-grid/with-gridsheet-renderer
    (e/server
      (hfql [hf/*$* hf/db]
        {(orders .)
         [:db/id
          :order/email
          :order/gender
          :order/shirt-size]}))))

(e/defn HFQL-demo [[F & args :as route]]
  (e/server
    (binding [hf/db user.orders-datomic/*$* ; hfql compiler
              hf/*nav!*   user.orders-datomic/nav! ; hfql compiler
              hf/*schema* user.orders-datomic/schema ; hfql gridsheet renderer
              ]
      (e/client
        (if-not F (Index. [])
          (F. args))))))

(e/defn Scratch [_]
  (e/client
    (dom/h1 (dom/text "hi"))
    (dom/pre (dom/text (pr-str (e/server ((e/partial-dynamic [hf/*$* hf/db] #(orders "")))))))
    (dom/pre (dom/text (pr-str (e/server (str hf/db)))))
    (dom/pre (dom/text (pr-str (e/server (hfql [hf/*$* hf/db] 42)))))
    (dom/pre (dom/text (pr-str (e/server (hfql [hf/*$* hf/db] :db/id 1)))))
    (hfql-tree-grid/with-gridsheet-renderer
      (e/server (hfql [hf/*$* hf/db] :db/id 1)))))

#?(:clj
   (tests
     (alter-var-root #'hf/*$* (constantly user.orders-datomic/*$*))
     (some? hf/*$*) := true
     (orders "") := [1 2 3]

     (with (e/run (tap (binding [hf/db hf/*$*
                                 hf/*nav!* user.orders-datomic/nav!]
                         (hfql [] :db/id 1))))
       % := 1)

     (with (e/run (tap (binding [hf/db hf/*$*
                                 hf/*nav!* user.orders-datomic/nav!]
                         (hfql []
                           {(orders "")
                            [:db/id
                             :order/email
                             :order/gender]}))))
       % := {`(orders "")
             [{:db/id 1, :order/email "alice@example.com", :order/gender :order/female}
              {:db/id 2, :order/email "bob@example.com", :order/gender :order/male}
              {:db/id 3, :order/email "charlie@example.com", :order/gender :order/male}]})))