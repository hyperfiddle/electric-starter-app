(ns electric-fiddle.read-src
  (:import (clojure.lang RT) (java.io InputStreamReader LineNumberReader PushbackReader))
  (:require [hyperfiddle.rcf :refer [tests]]
            [clojure.java.io :as io]))

(defn read-src
  "Returns a string of the source code for the given symbol, if it can find it. 
This requires that the symbol resolve to a Var defined in a namespace for which 
the .clj/cljs/cljc is in the classpath. Returns nil if it can't find the source.  
For most REPL usage, 'source' is more convenient. 

Example: (source-fn 'filter)"
  [x]
  (when-let [v (resolve x)]
    (when-let [filepath (:file (meta v))]
      (when-let [strm (.getResourceAsStream (RT/baseLoader) filepath)]
        (with-open [rdr (LineNumberReader. (InputStreamReader. strm))]
          (dotimes [_ (dec (:line (meta v)))] (.readLine rdr))
          (let [text (StringBuilder.)
                pbr (proxy [PushbackReader] [rdr]
                      (read [] (let [i (proxy-super read)]
                                 (.append text (char i))
                                 i)))
                read-opts (if (.endsWith ^String filepath "cljc") {:read-cond :allow} {})]
            (if (= :unknown *read-eval*)
              (throw (IllegalStateException. "Unable to read source while *read-eval* is :unknown."))
              (read read-opts (PushbackReader. pbr)))
            (str text)))))))

(tests
  (read-src `first)
  := "(def
 ^{:arglists '([coll])
   :doc \"Returns the first item in the collection. Calls seq on its
    argument. If coll is nil, returns nil.\"
   :added \"1.0\"
   :static true}
 first (fn ^:static first [coll] (. clojure.lang.RT (first coll))))")

(defn resolve-var-or-ns [sym]
  (if (qualified-symbol? sym)
    (resolve sym) (find-ns sym)))

(defn read-ns-src [sym]
  (some-> (resolve-var-or-ns sym) meta :file io/resource slurp))

(comment
  (resolve-var-or-ns 'electric-fiddle.read-src/read-src)
  (-> *1 meta :file)
  (read-ns-src 'electric-fiddle.read-src))
