(ns contrib.template
  #?(:cljs (:require-macros [contrib.template])
     :clj (:require clojure.java.io 
                    clojure.edn)))

(defmacro load-resource [filename]
  (some-> filename clojure.java.io/resource slurp clojure.edn/read-string))