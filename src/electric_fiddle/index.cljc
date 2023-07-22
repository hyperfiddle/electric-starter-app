(ns electric-fiddle.index
  (:require [hyperfiddle.electric :as e]
            
            ; fiddle database, todo dynamic
            [dustingetz.essay :refer [Essay]]
            [dustingetz.hfql-teeshirt-orders :refer [HFQL-demo]]
            [dustingetz.y-fib :refer [Y-fib]]
            [dustingetz.y-dir :refer [Y-dir]]
            [electric-fiddle.fiddle :refer [Fiddle]]))

; todo: macro to auto-install demos by attaching clj metadata to e/defn vars?
(e/def pages
  {`Fiddle Fiddle
   `Y-fib Y-fib
   `Y-dir Y-dir
   `Essay Essay
   `HFQL-demo HFQL-demo})
