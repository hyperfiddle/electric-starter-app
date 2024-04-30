(ns electric-starter-app.main
  (:require [hyperfiddle.electric-de :as e :refer [$]]
            [hyperfiddle.electric-dom3-efns :as dom]
            [hyperfiddle.electric.impl.lang-de2 :as lang]
            [hyperfiddle.electric.impl.runtime-de :as r]
            [hyperfiddle.incseq :as i]
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

;; #?(:clj (def !msgs (atom (list))))

;; #?(:cljs (defn enter [e]
;;            (when (= "Enter" (.-key e))
;;              (when-some [v (empty->nil (.substr (.. e -target -value) 0 100))]
;;                (set! (.. e -target -value) "")
;;                v))))

;; (e/defn Main [ring-request]
;;   (e/client
;;     (binding [dom/node js/document.body]
;;       (let [msgs (e/server (e/watch !msgs))]
;;         (dom/ul
;;           (e/cursor [msg (e/server (e/diff-by identity (reverse msgs)))] ; chat renders bottom up
;;             (dom/li
;;               (dom/style {:visibility (if (nil? msg) "hidden" "visible")})
;;               (dom/text msg))))
;;         (dom/input
;;           (dom/props {:placeholder "Type a message" :maxlength 100})
;;           (when-some [v (dom/listen "keydown" enter)]
;;             (e/server (swap! !msgs #(cons v (take 9 %))))))))))


;;;;;;;;;;;;;;;;;;
;;; PAY BUTTON ;;;
;;;;;;;;;;;;;;;;;;

#?(:clj (def !payments (atom [])))
(defn payT [_] (m/sp (m/? (m/sleep 1000)) (rand-int 1000)))
(defn task->incseq [T] (m/ap (m/amb (i/empty-diff 0) (assoc (i/empty-diff 1) :grow 1, :change {0 (m/? T)}))))

(e/defn Main [ring-request]
  (binding [dom/node js/document.body]
    (dom/button (dom/text "pay or else..")
      (let [[v done!] (e/join (dom/event->task (dom/listen* dom/node "click" hash)))]
        (case (e/server (swap! !payments conj (e/join (task->incseq (payT v)))))
          (done!))))
    (dom/ul
      (e/cursor [v (e/server (e/diff-by identity (e/watch !payments)))]
        (dom/li (dom/text "paid " v))))))

#_(e/defn Main [ring-request]
  (binding [dom/node js/document.body]
    (dom/button
      (dom/text "pay or else..")
      (let [[v done! busy?] (e/join (dom/event->task (dom/listen* dom/node "click" hash)))]
        (prn :v v :busy? busy?)
        ($ dom/Attribute dom/node :disabled busy?)
        #_(dom/props {:aria-busy busy?})
        (e/server (case (doto (swap! !payments conj (e/join (task->incseq (payT v)))) (prn :case)) (e/client (doto (done!) (prn :done!)))))))
    (dom/ul
      (e/cursor [v (e/server (e/diff-by identity (e/watch !payments)))]
        (dom/li (dom/text "paid " v))))))
