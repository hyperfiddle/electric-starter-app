(ns user.demo-system-properties
  "A web view that queries the backend Java environment and streams it to the
  DOM, all in one single composed expression"
  (:require
   [clojure.string :as str]
   [hyperfiddle.electric :as e]
   [hyperfiddle.electric-dom2 :as dom]
   [hyperfiddle.electric-ui4 :as ui]))

#?(:clj
   (defn jvm-system-properties [?s]
     (->> (System/getProperties)
       (map (juxt key val))
       (filter (fn [[k v]]
                 (str/includes? (str/lower-case (str k))
                   (str/lower-case (str ?s)))))
       (sort-by first))))

(e/defn SystemProperties []
  (e/client
    (let [!search (atom "")
          search (e/watch !search)]
      (e/server
        (let [system-props (e/offload #(jvm-system-properties search))
              matched-count (count system-props)]
          (e/client
            (dom/div (dom/text matched-count " matches"))
            (ui/input search (e/fn [v] (reset! !search v))
              (dom/props {:type "search" :placeholder "java.class.path"}))
            (dom/table
              (dom/tbody
                (e/server
                  ; reactive for, stabilized with "react key"
                  (e/for-by first [[k v] system-props]
                    (e/client
                      (dom/tr
                        (dom/td (dom/text k))
                        (dom/td (dom/text v))))))))))))))