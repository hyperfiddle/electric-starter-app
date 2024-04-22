(ns electric-starter-app.main
  (:require [hyperfiddle.electric-de :as e :refer [$]]
            [hyperfiddle.electric-dom3-efns :as dom]
            [hyperfiddle.electric.impl.lang-de2 :as lang]
            [clojure.string :as str]
            [contrib.data :refer [pad]]
            [contrib.str :refer [empty->nil]]
            [missionary.core :as m]))

;; Saving this file will automatically recompile and update in your browser

#?(:clj (defonce !x (atom true))) ; server state

;;;;;;;;;;;;;;;;;
;; TOGGLE DEMO ;;
;;;;;;;;;;;;;;;;;
;;
#_(e/defn Main [ring-request]
  (e/client
    (let [x (e/server (e/watch !x))]
      (binding [dom/node js/document.body]
        (dom/div
          (dom/text "number type here is: "
                    (case x
                      true (e/client (pr-str (type 1))) ; javascript number type
                      false (e/server (pr-str (type 1)))))) ; java number type

        (dom/div
          (dom/text "current site: "
                    (if x
                      "ClojureScript (client)"
                      "Clojure (server)")))

        (dom/button
          (e/server ((fn [_] (swap! !x not)) (e/client (dom/listen "click"))))
          (dom/text "toggle client/server"))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; SYSTEM PROPERTIES DEMO ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; #?(:clj
;;    (defn jvm-system-properties [?s]
;;      (->> (System/getProperties)
;;        (into {})
;;        (filter (fn [[k _v]]
;;                  (str/includes? (str/lower-case (str k))
;;                                 (str/lower-case (str ?s)))))
;;        (sort-by first))))

;; (e/defn Main [ring-request]
;;   (e/client
;;     (binding [dom/node js/document.body]
;;       (let [!search (atom "")
;;             search (e/watch !search)
;;             system-props (e/server (jvm-system-properties search))
;;             matched-count (e/server (count system-props))]
;;         (dom/div (dom/text matched-count " matches"))
;;         (dom/input
;;           (dom/props {:type "search" :placeholder "🔎  java.class.path"})
;;           (dom/listen "input" #(reset! !search (-> % .-target .-value))))
;;         (dom/table
;;           (dom/tbody
;;             (e/cursor [[k v] (e/server (doto (e/diff-by key system-props) prn))]
;;               (println 'rendering k)
;;               (dom/tr
;;                 (dom/td (dom/text k))
;;                 (dom/td (dom/text v))))))))))

;;;;;;;;;;
;; CHAT ;;
;;;;;;;;;;

#?(:clj (def !msgs (atom (list))))

#?(:cljs (defn enter [e]
           (when (= "Enter" (.-key e))
             (when-some [v (empty->nil (.substr (.. e -target -value) 0 100))]
               (set! (.. e -target -value) "")
               v))))

(e/defn Main [ring-request]
  (e/client
    (binding [dom/node js/document.body]
      (let [msgs (e/server (e/watch !msgs))]
        (dom/ul
          (e/cursor [msg (e/server (e/diff-by identity (reverse msgs)))] ; chat renders bottom up
            (dom/li
              (dom/style {:visibility (if (nil? msg) "hidden" "visible")})
              (dom/text msg))))
        (dom/input
          (dom/props {:placeholder "Type a message" :maxlength 100})
          (when-some [v (dom/listen "keydown" enter)]
            (e/server (swap! !msgs #(cons v (take 9 %))))))))))
