(ns dustingetz.dustingetz-registry
  (:require [hyperfiddle.electric :as e]
            
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
            [electric-fiddle.essay :refer [Essay]]))

(e/def pages
  {`Y-fib Y-fib
   `Y-dir Y-dir
   `Essay Essay
   `HFQL-demo-wrap HFQL-demo-wrap ; Datomic
   `Teeshirt-orders-1 Teeshirt-orders-1
   `Teeshirt-orders-2 Teeshirt-orders-2
   `Teeshirt-orders-3 Teeshirt-orders-3
   `Teeshirt-orders-4 Teeshirt-orders-4
   `Teeshirt-orders-5 Teeshirt-orders-5
   `DirectoryExplorer-HFQL DirectoryExplorer-HFQL
   `Bug-unmount Bug-unmount})