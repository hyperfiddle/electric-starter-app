(ns dustingetz.hfql-teeshirt-orders
  (:require [hyperfiddle.api :as hf]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.hfql :refer [hfql]]
            [hyperfiddle.hfql-tree-grid :refer [with-gridsheet-renderer]]
            #?(:clj [hfql-demo.model-teeshirt-orders-datomic :as model
                     #_#_:refer [orders genders shirt-sizes]])))

(e/defn Teeshirt-orders []
  (hfql [hf/*$* hf/db]
    {(model/orders .)
     [:db/id
      :order/email
      (props :order/gender {::hf/options (model/genders)
                            ::hf/option-label (e/fn [x] (name x))})
      (props :order/shirt-size {::hf/options (model/shirt-sizes order/gender .) 
                                ::hf/option-label (e/fn [x] (name x))})]}))

(e/defn Webview-HFQL [_]
  (e/client
    (with-gridsheet-renderer
      (e/server
        (binding [hf/db model/*$* ; hfql compiler
                  hf/*nav!*   model/nav! ; hfql compiler
                  hf/*schema* model/schema ; hfql gridsheet renderer
                  ]
          (Teeshirt-orders.))))))
