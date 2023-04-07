(ns wip.teeshirt-orders
  (:require [clojure.spec.alpha :as s]
            #?(:clj [datascript.core :as d])
            #?(:clj [datascript.impl.entity :refer [entity?]])
            [hyperfiddle.api :as hf]
            [hyperfiddle.hfql :refer [hfql]]
            [hyperfiddle.hfql-tree-grid :as hfql-tree-grid]
            [hyperfiddle.electric :as e]))

(declare conn schema nav orders)
(def ^:dynamic db)

(e/defn Teeshirt-orders []
  (hfql [db hf/db] ; convey reactive db to clojure dynamic
    {(orders .)
     [:db/id
      :order/email
      :order/gender]}))

(s/fdef orders :args (s/cat :needle string?) :ret (s/coll-of any?))
(defn orders [needle]
  #?(:clj
     (sort (d/q '[:find [?e ...] :in $ ?needle :where
                  [?e :order/email ?email]
                  [(clojure.string/includes? ?email ?needle)]]
             db (or needle "")))))

(e/defn Webview-HFQL []
  (e/client
    (hfql-tree-grid/with-gridsheet-renderer
      (e/server
        (binding [hf/db (e/watch conn)
                  hf/*schema* schema
                  hf/*nav!*   nav]
          (Teeshirt-orders.))))))

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