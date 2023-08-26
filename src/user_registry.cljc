(ns user-registry
  (:require [hyperfiddle.electric :as e]
            
            ; fiddle database, todo dynamic
            [dustingetz.demo-explorer-hfql :refer [DirectoryExplorer-HFQL]]
            [dustingetz.y2023.bug-unmount :refer [Bug-unmount]]
            [dustingetz.hfql-intro :refer [HFQL-demo-wrap
                                           Teeshirt-orders-1
                                           Teeshirt-orders-2
                                           Teeshirt-orders-3
                                           Teeshirt-orders-4
                                           Teeshirt-orders-5]]
            [dustingetz.y-fib :refer [Y-fib]]
            [dustingetz.y-dir :refer [Y-dir]]
            [electric-fiddle.essay :refer [Essay]]
            [electric-fiddle.fiddle :refer [Fiddle]]
            #_[electric-fiddle.index :refer [Index]]
            [hfql-demo.hfql-teeshirt-orders :refer [HFQL-teeshirt-orders]]))

; todo: macro to auto-install demos by attaching clj metadata to e/defn vars?
(e/def pages
  {;`Index Index -- don't list in index
   `Fiddle Fiddle
   `Y-fib Y-fib
   `Y-dir Y-dir
   `Essay Essay
   `HFQL-demo-wrap HFQL-demo-wrap
   `HFQL-teeshirt-orders HFQL-teeshirt-orders
   `Teeshirt-orders-1 Teeshirt-orders-1
   `Teeshirt-orders-2 Teeshirt-orders-2
   `Teeshirt-orders-3 Teeshirt-orders-3
   `Teeshirt-orders-4 Teeshirt-orders-4
   `Teeshirt-orders-5 Teeshirt-orders-5 
   `DirectoryExplorer-HFQL DirectoryExplorer-HFQL
   `Bug-unmount Bug-unmount
   })
