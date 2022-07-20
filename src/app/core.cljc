(ns app.core
  (:require [datascript.core :as d]
            [hyperfiddle.photon :as p]
            [hyperfiddle.photon-dom :as dom]
            [hyperfiddle.photon-ui :as ui])
  #?(:cljs (:require-macros app.core)))

(defonce !conn #?(:clj (d/create-conn {}) :cljs nil))

(p/defn TodoCreate []
  (ui/input {:dom/placeholder    " Buy milk ⏎"
             :dom/type           "text"
             ::ui/keychord-event [#{"enter"}
                                  (p/fn [js-event]
                                    (when js-event
                                      (let [dom-node (:target js-event)
                                            value    (:value dom-node)]
                                        (dom/oset! dom-node :value "")
                                        (p/remote (do (d/transact! !conn [{:task/description value
                                                                           :task/status      :active}])
                                                      nil)))))]}))

(p/defn TodoItem [e]
  (let [id          (:db/id e)
        status      (:task/status e)
        description (:task/description e)]
    (p/remote
      (do (ui/checkbox {:dom/id          id
                        :dom/checked     (case status :active false, :done true)
                        ::ui/input-event (p/fn [js-event]
                                           (when js-event
                                             (let [done? (-> js-event :target :checked)]
                                               (p/remote
                                                 (do (d/transact! !conn [{:db/id       (p/deduping id)
                                                                          :task/status (if done? :done :active)}])
                                                     nil)))))})
          (dom/label {:dom/for id} (dom/text (str description)))))))

(defn todo-count [db]
  (count (d/q '[:find [?e ...] :in $ ?status :where [?e :task/status ?status]] db :active)))

(p/defn Todo-list []
  (let [db (p/watch !conn)]
    (p/remote
      (do (dom/h1 (dom/text "Todo list (Photon demo)"))
          (dom/ul
            (dom/li (dom/text "Full stack webapp in one file"))
            (dom/li (dom/text "Server side database"))
            (dom/li (dom/text "Rich client rendering"))
            (dom/li (dom/text "Collaborative — try multiple tabs")))
          (dom/div {:dom/class "todo-list"}
            (TodoCreate.)
            (dom/div {:dom/class "todo-items"}
              (p/for [id (p/remote (d/q '[:find [?e ...] :in $ :where [?e :task/status]] db))]
                (p/remote (TodoItem. (d/entity db id) id))))
            (dom/p {:dom/class "counter"}
              (dom/span {:dom/class "count"} (dom/text (p/remote (todo-count db))))
              (dom/text " items left")))))))

