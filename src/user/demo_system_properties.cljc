(ns user.demo-system-properties
  (:require [clojure.string :as str] 
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui]))

#?(:clj
   (defn jvm-system-properties [?s]
     (->> (System/getProperties)
       (filter (fn [^java.util.concurrent.ConcurrentHashMap$MapEntry kv]
                 (str/includes? (str/lower-case (str (key kv))) 
                                (str/lower-case (str ?s)))))
       (sort-by key))))

(e/defn SystemProperties []
  (e/client
    (let [!search (atom "")
          search (e/watch !search)]
      (e/server
        (let [system-props (jvm-system-properties search)
              matched-count (count system-props)]
          (e/client
            (dom/div (dom/text matched-count " matches"))
            (ui/input search (e/fn [v] (reset! !search v))
              (dom/props {:type "search" :placeholder "java.class.path"}))
            (dom/table
              (dom/tbody
                (e/server
                  ; reactive for, stabilized with "react key"
                  (e/for-by key [[k v] system-props]
                    (e/client
                      (println 'rendering k)
                      (dom/tr
                        (dom/td (dom/text k))
                        (dom/td (dom/text v))))))))))))))