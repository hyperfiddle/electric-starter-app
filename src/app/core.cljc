(ns app.core
  (:require #?(:clj [datascript.core :as d])
            [hyperfiddle.photon :as p]
            [hyperfiddle.photon-dom :as dom]
            [hyperfiddle.photon-ui :as ui])
  #?(:cljs (:require-macros app.core)))

(defonce !conn #?(:clj (d/create-conn {}) :cljs nil))
(p/def db (p/watch !conn))

(p/defn TodoCreate []
  (ui/input {::dom/placeholder   "Buy milk"
             ::dom/type          "text"
             ::ui/keychords      #{"enter"}
             ::ui/keychord-event (p/fn [e]
                                   (p/client
                                     (let [value (:value dom/node)]
                                       (p/server (d/transact! !conn [{:task/description value
                                                                      :task/status      :active}]))
                                       (dom/oset! dom/node :value ""))))}))

(p/defn TodoItem [id]
  (p/server
    (let [e (d/entity db id)
          status (:task/status e)]
      (p/client
        (ui/checkbox {::dom/id         id
                      ::ui/value       (case status :active false, :done true)
                      ::ui/input-event (p/fn [js-event]
                                         (let [status (-> js-event :target :checked)]
                                           (p/server
                                             (d/transact! !conn [{:db/id       id
                                                                  :task/status (if status :done :active)}]))))})
        (dom/label {::dom/for id} (p/server (:task/description e)))))))

#?(:clj
   (defn todo-count [db]
     (count (d/q '[:find [?e ...] :in $ ?status :where [?e :task/status ?status]] db :active))))

(p/defn Todo-list []
  (p/client
    (dom/div {::dom/class "todo-list"}
      (TodoCreate.)
      (dom/div {::dom/class "todo-items"}
        (p/server
          (p/for [id (d/q '[:find [?e ...] :in $ :where [?e :task/status]] db)]
            (TodoItem. id))))
      (dom/p {::dom/class "counter"}
        (dom/span {::dom/class "count"} (p/server (todo-count db)))
        " items left"))))
