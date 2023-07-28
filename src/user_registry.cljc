(ns user-registry
  (:require [hyperfiddle.electric :as e]
            
            ; fiddle database, todo dynamic
            [dustingetz.hfql-intro :refer [HFQL-demo
                                           Teeshirt-orders
                                           Teeshirt-orders-2
                                           Teeshirt-orders-3
                                           Teeshirt-orders-4
                                           Teeshirt-orders-5]]
            [dustingetz.hfql-teeshirt-orders :refer [Webview-HFQL]]
            [dustingetz.y-fib :refer [Y-fib]]
            [dustingetz.y-dir :refer [Y-dir]]
            [electric-fiddle.essay :refer [Essay]]
            [electric-fiddle.fiddle :refer [Fiddle]]
            [electric-fiddle.index :refer [Index]]))

; todo: macro to auto-install demos by attaching clj metadata to e/defn vars?
(e/def pages
  {`Index Index
   `Fiddle Fiddle
   `Y-fib Y-fib
   `Y-dir Y-dir
   `Essay Essay
   `HFQL-demo HFQL-demo
   `Teeshirt-orders Teeshirt-orders
   `Teeshirt-orders-2 Teeshirt-orders-2
   `Teeshirt-orders-3 Teeshirt-orders-3
   `Teeshirt-orders-4 Teeshirt-orders-4
   `Teeshirt-orders-5 Teeshirt-orders-5
   `Webview-HFQL Webview-HFQL
   })
