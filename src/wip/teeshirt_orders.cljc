(ns wip.teeshirt-orders
  (:require #?(:clj [user.example-datascript-db :as db])
            [hyperfiddle.api :as hf]
            [hyperfiddle.hfql-tree-grid :as ttgui]
            [hyperfiddle.electric :as e]
            wip.orders-datascript))

(e/defn Webview-HFQL []
  (e/client
    (ttgui/with-gridsheet-renderer
      (e/server
        (binding [hf/db       hf/*$*
                  hf/*schema* db/get-schema
                  hf/*nav!*   db/nav!]
          (hf/hfql
            {(wip.orders-datascript/orders .)
             [:db/id
              :order/email
              :order/gender
              :order/shirt-size]}
            ))))))