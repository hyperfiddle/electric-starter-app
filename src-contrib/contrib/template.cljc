(ns contrib.template
  #?(:cljs (:require-macros [contrib.template :refer [comptime-resource]]))
  (:require clojure.edn
            #?(:clj clojure.java.io)
            clojure.string
            [hyperfiddle.rcf :refer [tests]]))

(defmacro comptime-resource [filename]
  (some-> filename clojure.java.io/resource slurp clojure.edn/read-string))

(defn template 
  "In string template `<div>$:foo/bar$</div>`, replace all instances of $key$ 
with target specified by map `m`. Target values are coerced to string with `str`."
  [t m] (reduce-kv (fn [acc k v] (clojure.string/replace acc (str "$" k "$") (str v))) t m))

(tests
  (template "<!-- $:foo/bar$ $:baz$ $:buzz$ $strkey$ -->" 
    {:foo/bar true :baz "hi" :buzz nil "strkey" 42 :extra 99})
  := "<!-- true hi  42 -->")