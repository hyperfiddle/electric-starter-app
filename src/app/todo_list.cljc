(ns app.todo-list
  (:require contrib.str
            #?(:clj [datascript.core :as d]) ; database on server
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui]))

#?(:clj (defonce !conn (d/create-conn {}))) ; database on server
(e/def db) ; injected database ref; Electric defs are always dynamic

(e/defn TodoItem [id]
  (e/server
    (let [e (d/entity db id)
          status (:task/status e)]
      (e/client
        (dom/div
          (ui/checkbox
            (case status :active false, :done true)
            (e/fn [v]
              (e/server
                (d/transact! !conn [{:db/id id
                                     :task/status (if v :done :active)}])
                nil))
            (dom/props {:id id}))
          (dom/label (dom/props {:for id}) (dom/text (e/server (:task/description e)))))))))

(e/defn InputSubmit [F]
  ; Custom input control using lower dom interface for Enter handling
  (e/client
    (dom/input (dom/props {:placeholder "Buy milk"})
      (dom/on "keydown" (e/fn [e]
                          (when (= "Enter" (.-key e))
                            (when-some [v (contrib.str/empty->nil (-> e .-target .-value))]
                              (new F v)
                              (set! (.-value dom/node) ""))))))))

(e/defn TodoCreate []
  (e/client
    (InputSubmit. (e/fn [v]
                    (e/server
                      (d/transact! !conn [{:task/description v
                                           :task/status :active}])
                      nil)))))

#?(:clj (defn todo-count [db]
          (count
            (d/q '[:find [?e ...] :in $ ?status
                   :where [?e :task/status ?status]] db :active))))

#?(:clj (defn todo-records [db]
          (->> (d/q '[:find [(pull ?e [:db/id :task/description]) ...]
                      :where [?e :task/status]] db)
            (sort-by :task/description))))

(e/defn Todo-list []
  (e/client
    (binding [dom/node js/document.body]
      (e/server
        (binding [db (e/watch !conn)]
          (e/client
            (dom/link (dom/props {:rel :stylesheet :href "/todo-list.css"}))
            (dom/h1 (dom/text "minimal todo list"))
            (dom/p (dom/text "it's multiplayer, try two tabs"))
            (dom/div (dom/props {:class "todo-list"})
                     (TodoCreate.)
                     (dom/div {:class "todo-items"}
                              (e/server
                                (e/for-by :db/id [{:keys [db/id]} (todo-records db)]
                                  (TodoItem. id))))
                     (dom/p (dom/props {:class "counter"})
                            (dom/span (dom/props {:class "count"}) (dom/text (e/server (todo-count db))))
                            (dom/text " items left")))))))))
