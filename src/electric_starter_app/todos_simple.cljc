(ns electric-starter-app.todos-simple
  (:require #?(:clj [datascript.core :as d])
            [hyperfiddle.electric-de :as e :refer [$]]
            [hyperfiddle.electric-dom3 :as dom]))

#?(:clj (defonce !conn (d/create-conn {}))) ; database on server
(declare db)                                ; injected db ref

(e/defn TodoItem [id]
  (let [e (e/server (d/entity db id))
        status (e/server (:task/status e))]
    (dom/li
      (dom/input
        (dom/props {:type "checkbox", :id id})
        (let [v ($ dom/On "change" #(-> % .-target .-checked))
              spend! ($ e/Token v)]
          (when-not (or spend! ($ dom/Focused?))
            (set! (.-checked dom/node) (case status :active false, :done true)))
          (when spend!
            (spend! (e/server
                      ({} (d/transact! !conn [{:db/id id, :task/status (if v :done :active)}])
                       nil))))))
      (dom/label
        (dom/props {:for id})
        (dom/text (e/server (:task/description e)))))))

#?(:cljs (defn read! [node] (when-some [v (not-empty (.-value node))] (set! (.-value node) "") v)))
#?(:cljs (defn enter [e] (when (= "Enter" (.-key e)) (read! (.-target e)))))

(e/defn InputSubmit [F]
  (dom/input
    (dom/props {:placeholder "Buy milk", :maxLength 100})
    (e/cursor [[v spend!] ($ dom/OnAll "keydown" enter)]
      (spend! ($ F (subs v 0 100))))))

(e/defn TodoCreate []
  ($ InputSubmit
    (e/fn [v]
      ({} (e/server (d/transact! !conn [{:task/description v, :task/status :active}])) nil))))

#?(:clj (defn todo-count [db]
          (count
            (d/q '[:find [?e ...] :in $ ?status
                   :where [?e :task/status ?status]]
              db :active))))

#?(:clj (defn todo-records [db]
          (->> (d/q '[:find [(pull ?e [:db/id :task/description]) ...]
                      :where [?e :task/status]] db)
            (sort-by :task/description))))

(e/defn TodoList []
  (binding [db (e/server (e/watch !conn))]
    (dom/div
      (dom/props {:class "todo-list"})
      ($ TodoCreate)
      (dom/ul
        (dom/props {:class "todo-items"})
        (e/cursor [{:keys [db/id]} (e/server (e/diff-by :db/id ($ e/Offload #(todo-records db))))]
          ($ TodoItem id)))
      (dom/p
        (dom/props {:class "counter"})
        (dom/span
          (dom/props {:class "count"})
          (dom/text (e/server ($ e/Offload #(todo-count db)))))
        (dom/text " items left")))))
