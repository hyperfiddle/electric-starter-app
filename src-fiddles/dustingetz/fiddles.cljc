(ns dustingetz.fiddles
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle :as hf]
            [dustingetz.demo-explorer-hfql :refer [DirectoryExplorer-HFQL]]
            [dustingetz.hfql-intro :refer [With-HFQL-Bindings
                                           Teeshirt-orders-1
                                           Teeshirt-orders-2
                                           Teeshirt-orders-3
                                           Teeshirt-orders-4
                                           Teeshirt-orders-5]]
            dustingetz.scratch
            [dustingetz.y-fib :refer [Y-fib]]
            [dustingetz.y-dir :refer [Y-dir]]
            [dustingetz.essay :refer [Essay]]
            [electric-fiddle.main]
            #?(:clj models.teeshirt-orders-datomic)
            ))

(e/def fiddles
  {`Y-fib Y-fib
   `Y-dir Y-dir
   `Essay (With-HFQL-Bindings. Essay)
   `Teeshirt-orders-1 (With-HFQL-Bindings. Teeshirt-orders-1)
   `Teeshirt-orders-2 (With-HFQL-Bindings. Teeshirt-orders-2)
   `Teeshirt-orders-3 (With-HFQL-Bindings. Teeshirt-orders-3)
   `Teeshirt-orders-4 (With-HFQL-Bindings. Teeshirt-orders-4)
   `Teeshirt-orders-5 (With-HFQL-Bindings. Teeshirt-orders-5)
   `DirectoryExplorer-HFQL (With-HFQL-Bindings. DirectoryExplorer-HFQL)
   `dustingetz.scratch/Scratch dustingetz.scratch/Scratch})

#?(:clj
   (models.teeshirt-orders-datomic/init-datomic))

(e/defn FiddleMain [ring-req]
  (e/client
    (binding [hf/pages fiddles]
      (e/server
        (electric-fiddle.main/Main. ring-req)))))

