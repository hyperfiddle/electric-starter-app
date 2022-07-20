(ns app.core
  (:require [datascript.core :as d]
            [hyperfiddle.photon :as p]
            [hyperfiddle.photon-dom :as dom]
            [hyperfiddle.photon-ui :as ui])
  #?(:cljs (:require-macros app.core)))

(defonce !conn #?(:clj (d/create-conn {}) :cljs nil))

(p/defn TodoCreate []
  (ui/input {:dom/placeholder    "Buy milk ⏎"
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
      (do (ui/checkbox {:dom/checked     (case status :active false, :done true)
                        ::ui/input-event (p/fn [js-event]
                                           (when js-event
                                             (let [done? (-> js-event :target :checked)]
                                               (p/remote
                                                 (do (d/transact! !conn [{:db/id       (p/deduping id)
                                                                          :task/status (if done? :done :active)}])
                                                     nil)))))})
          (dom/span (dom/text (str description)))))))

(defn todo-count [db]
  (count (d/q '[:find [?e ...] :in $ ?status :where [?e :task/status ?status]] db :active)))

(p/defn Todo-list []
  (let [db (p/watch !conn)]
    (p/remote
      (dom/div
        (dom/h1 (dom/text "Todo list (Photon demo)"))
        (dom/p (dom/text "Full stack webapp in one file.") (dom/br)
          (dom/text "Server side database, rich client rendering") (dom/br)
          (dom/text "Collaborative – try multiple tabs"))
        (TodoCreate.)
        (dom/div
          (p/for [id (p/remote (d/q '[:find [?e ...] :in $ :where [?e :task/status]] db))]
            (dom/label
              (dom/set-style! dom/node :display "block")
              (p/remote (TodoItem. (d/entity db id) id)))))
        (dom/p (dom/text (p/remote (todo-count db)) " items left"))))))

