(ns dustingetz.y-dir
  (:require [contrib.str :refer [includes-str?]]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui]
            dustingetz.y-fib))

#?(:clj (defn file-is-dir [h] (.isDirectory h)))
#?(:clj (defn file-is-file [h] (.isFile h)))
#?(:clj (defn file-list-files [h] (.listFiles h)))
#?(:clj (defn file-get-name [h] (.getName h)))
#?(:clj (defn file-absolute-path [^String path-str & more]
          (-> (java.nio.file.Path/of ^String path-str (into-array String more))
            .toAbsolutePath str)))

(e/defn Y [Gen]
  (new (e/fn [F] (F. F))
    (e/fn F [F]
      (Gen. (e/fn Recur [x]
              (new (F. F) x))))))

(e/defn Dir-tree [Recur]
  (e/fn [[h s]]
    (cond
      (file-is-dir h) 
      (e/client
        (dom/li (dom/text (e/server (file-get-name h)))
          (dom/ul
            (e/server
              (e/for [x (file-list-files h)]
                (Recur. [x s]))))))              ; recur
      
      (file-is-file h)
      (when (includes-str? (file-get-name h) s)
        (let [name_ (e/server (file-get-name h))]
          (e/client (dom/li (dom/text name_))))))))

(e/defn Y-dir []
  (dom/div
    (let [!s (atom "") s (e/watch !s)]
      (ui/input s (e/fn [v] (reset! !s v))) 
      (dom/ul
        (e/server
          (let [h (clojure.java.io/file (file-absolute-path "./src"))]
            (new (Y. Dir-tree) [h s])))))))
