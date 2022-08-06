(ns app.core
  (:require [datascript.core :as d]
            [hyperfiddle.photon :as p]
            [hyperfiddle.photon-dom :as dom]
            [hyperfiddle.photon-ui :as ui])
  #?(:cljs (:require-macros app.core)))

(defonce !conn #?(:clj (d/create-conn {}) :cljs nil))

(p/defn TodoCreate []
  (ui/input {::dom/placeholder    "Buy milk"
             ::dom/type           "text"
             ::ui/keychords      #{"enter"}
             ::ui/keychord-event (p/fn [js-event]
                                    (let [v (:value dom/node)]
                                      (p/server (d/transact! !conn [{:task/description v
                                                                     :task/status      :active}])))
                                    (dom/oset! dom/node :value ""))}))

(p/defn TodoItem [e]
  (let [id          (:db/id e)
        status      (:task/status e)]
    (p/client
      (ui/checkbox {::dom/id          id
                    ::dom/checked     (case status :active false, :done true)
                    ::ui/input-event (p/fn [js-event]
                                       (let [done? (:checked dom/node)]
                                          (p/server (d/transact! !conn [{:db/id       id
                                                                          :task/status (if done? :done :active)}]) nil)))})
      (dom/label {:for id} (dom/text (str (p/server (:task/description e))))))))

(defn todo-count [db]
  #?(:clj (count (d/q '[:find [?e ...] :in $ ?status :where [?e :task/status ?status]] db :active))))

(p/defn Todo-list []
  (p/server
   (let [db (p/watch !conn)]
     (p/client
      (dom/h1 (dom/text "Multiplayer todo list in one page of code"))
      (dom/div {:dom/class "todo-list"}
        (TodoCreate.)
        (dom/div {:dom/class "todo-items"}
          (p/for [id (p/server (d/q '[:find [?e ...] :in $ :where [?e :task/status]] db))]
            (p/server (TodoItem. (d/entity db id) id))))
        (dom/p {:dom/class "counter"}
          (dom/span {:dom/class "count"} (dom/text (p/server (todo-count db))))
          (dom/text " items left")))))))
