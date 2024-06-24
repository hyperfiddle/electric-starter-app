(ns electric-starter-app.seven-guis-7-crud
  (:require
   [clojure.string :as str]
   [hyperfiddle.electric-de :as e :refer [$]]
   [hyperfiddle.electric-dom3 :as dom]))

(def !state (atom {:selected nil
                   :stage {:name ""
                           :surname ""}
                   :names (sorted-map 0 {:name "Emil", :surname "Hans"})}))

(def next-id (partial swap! (atom 0) inc))

(defn select! [id]
  (swap! !state (fn [state]
                  (assoc state :selected id
                               :stage (get-in state [:names id])))))

(defn set-name! [name]
  (swap! !state assoc-in [:stage :name] name))

(defn set-surname! [surname]
  (swap! !state assoc-in [:stage :surname] surname))

(defn create! []
  (swap! !state (fn [{:keys [stage] :as state}]
                  (-> state
                    (update :names assoc (next-id) stage)
                    (assoc :stage {:name "", :surname ""})))))
(defn delete! []
  (swap! !state (fn [{:keys [selected] :as state}]
                  (update state :names dissoc selected))))

(defn update! []
  (swap! !state (fn [{:keys [selected stage] :as state}]
                  (assoc-in state [:names selected] stage))))

(defn filter-names [names-map needle]
  (if (empty? needle)
    names-map
    (let [needle (str/lower-case needle)]
      (reduce-kv (fn [r k {:keys [name surname]}]
                   (if (or (str/includes? (str/lower-case name) needle)
                         (str/includes? (str/lower-case surname) needle))
                     r
                     (dissoc r k)))
        names-map names-map))))

(e/defn CRUD []
  (let [state (e/watch !state), selected (:selected state)]
    (dom/div
      (dom/props {:style {:display "grid", :grid-gap "0.5rem", :align-items "baseline"
                          :grid-template-areas "'a b c c'\n
                                                'd d e f'\n
                                                'd d g h'\n
                                                'd d i i'\n
                                                'j j j j'"}})
      (dom/span
        (dom/props {:style {:grid-area "a"}})
        (dom/text "Filter prefix:"))
      (let [!needle (atom ""), needle (e/watch !needle)]
        (dom/input
          (dom/props {:style {:grid-area "b"}})
          ($ dom/On "input" (fn [e] (reset! !needle (-> e .-target .-value)))))
        (dom/ul
          (dom/props {:style {:grid-area "d", :background-color "white"
                              :line-style-type "none", :padding 0
                              :border "1px gray solid", :height "100%"}})
          (e/cursor [entry (e/diff-by key (filter-names (:names state) needle))]
            (prn (type entry) entry)
            (let [id (key entry), value (val entry)]
              (dom/li
                (dom/text (:surname value) ", " (:name value))
                (dom/props {:style {:cursor "pointer", :padding "0.1rem 0.5rem"
                                    :color (if (= selected id) "white" "inherit")
                                    :background-color (if (= selected id) "blue" "inherit")}})
                ($ dom/On "click" (fn [_] (select! id))))))))
      (let [stage (:stage state)]
        (dom/span (dom/props {:style {:grid-area "e"}}) (dom/text "Name:"))
        (dom/input
          (dom/props {:style {:grid-area "f"}})
          ($ dom/On "input" (fn [e] (set-name! (-> e .-target .-value))))
          (when-not ($ dom/Focused?) (set! (.-value dom/node) (:name stage))))
        (dom/span (dom/props {:style {:grid-area "g"}}) (dom/text "Surname:"))
        (dom/input
          (dom/props {:style {:grid-area "h"}})
          ($ dom/On "input" (fn [e] (set-surname! (-> e .-target .-value))))
          (when-not ($ dom/Focused?) (set! (.-value dom/node) (:surname stage)))))
      (dom/div
        (dom/props {:style {:grid-area "j", :display "grid", :grid-gap "0.5rem"
                            :grid-template-columns "auto auto auto 1fr"}})
        (dom/button (dom/text "Create") ($ dom/On "click" (fn [_] (create!))))
        (dom/button (dom/text "Update") ($ dom/On "click" (fn [_] (update!)))
          (dom/props {:disabled (not selected)}))
        (dom/button (dom/text "Delete") ($ dom/On "click" (fn [_] (delete!)))
          (dom/props {:disabled (not selected)}))))))
