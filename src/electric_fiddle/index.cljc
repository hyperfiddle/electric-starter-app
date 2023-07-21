(ns electric-fiddle.index
  (:require [hyperfiddle.electric :as e]
            
            ; fiddle database, todo dynamic
            dustingetz.essay
            dustingetz.y-fib
            dustingetz.y-dir)) 

; todo: macro to auto-install demos by attaching clj metadata to e/defn vars?
(e/def pages
  {`dustingetz.y-fib/Demo-Y-fib dustingetz.y-fib/Demo-Y-fib
   `dustingetz.y-dir/Demo-Y-dir dustingetz.y-dir/Demo-Y-dir
   `dustingetz.essay/Essay dustingetz.essay/Essay})
