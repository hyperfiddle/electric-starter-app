(ns electric-fiddle.registry
  (:require [hyperfiddle.electric :as e]
            
            ; fiddle database, todo dynamic
            [dustingetz.essay :refer [Essay]]
            [dustingetz.hfql-teeshirt-orders :refer [HFQL-demo Teeshirt-orders]]
            [dustingetz.y-fib :refer [Y-fib]]
            [dustingetz.y-dir :refer [Y-dir]]
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
   `Teeshirt-orders Teeshirt-orders})
