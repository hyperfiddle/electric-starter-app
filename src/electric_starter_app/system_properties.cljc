(ns electric-starter-app.system-properties
  (:require
   [clojure.string :as str]
   [hyperfiddle.electric-de :as e :refer [$]]
   [hyperfiddle.electric-dom3 :as dom]))

#?(:clj
   (defn jvm-system-properties [?s]
     (->> (System/getProperties)
       (into {})
       (filter (fn [[k _v]]
                 (str/includes? (str/lower-case (str k))
                   (str/lower-case (str ?s)))))
       (sort-by first))))

(e/defn SystemProperties []
  (let [!search (atom ""), search (e/watch !search)
        system-props (e/server (jvm-system-properties search))
        matched-count (e/server (count system-props))]
    (dom/div (dom/text matched-count " matches"))
    (dom/input
      (dom/props {:type "search", :placeholder "🔎  java.class.path"})
      ($ dom/On "input" #(reset! !search (-> % .-target .-value))))
    (dom/table
         (dom/tbody
           (e/cursor [[k v] (e/server (e/diff-by key system-props))]
             (println 'rendering k v)
             (dom/tr (dom/td (dom/text k)) (dom/td (dom/text v))))))))
