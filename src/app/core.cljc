(ns app.core
  (:require [datascript.core :as d]
            [hyperfiddle.photon :as p]
            [hyperfiddle.photon-dom :as dom]
            [hyperfiddle.photon-ui :as ui])
  (:import [hyperfiddle.photon Pending]
           [missionary Cancelled])
  #?(:cljs (:require-macros app.core)))

(def global-auto-inc (partial swap! (atom 0) inc))
(defonce !conn #?(:clj (d/create-conn {})
                  :cljs nil))

(defn transact! [conn entity]
  (d/transact! conn [entity])
  nil)

(p/defn Todo-list []
  (let [db (p/watch !conn)]
    ~@(dom/div
        (dom/h1 (dom/text "Todo list - Collaborative"))
        (ui/input {:dom/placeholder    "Press enter to create a new item"
                   ::ui/keychord-event [#{"enter"} (p/fn [js-event]
                                                     (when js-event
                                                       (let [dom-node (:target js-event)
                                                             value    (:value dom-node)]
                                                         (dom/oset! dom-node :value "")
                                                         ~@(transact! !conn {:db/id            (global-auto-inc)
                                                                             :task/description value
                                                                             :task/status      :active}))))]})

        (dom/div
          (p/for [id ~@(d/q '[:find [?e ...] :in $ :where [?e :task/status]] db)]
            (dom/label
              (dom/set-style! dom/node :display "block")
              ~@(let [e      (d/entity db id)
                      status (:task/status e)]
                  ~@(do (ui/checkbox {:dom/checked     (case status :active false, :done true)
                                      ::ui/input-event (p/fn [js-event]
                                                         (when js-event
                                                           (let [done? (dom/oget js-event :target :checked)]
                                                             ~@(transact! !conn {:db/id       id
                                                                                 :task/status (if done? :done :active)}))))})
                        (dom/span (dom/text (str ~@(:task/description e)))))))))
        (dom/p (dom/text (str ~@(count (d/q '[:find [?e ...] :in $ ?status :where [?e :task/status ?status]] db :active))
                              " items left"))))))

(def main #?(:cljs (p/client (p/main (try (binding [dom/node (dom/by-id "root")]
                                            ~@(Todo-list.))
                                          (catch Pending _))))))
#?(:cljs
   (do
     (def reactor)
     (defn ^:dev/after-load start! [] (set! reactor (main js/console.log js/console.error)))
     ;; TODO: keep seeing `missionary.Cancelled {message: 'Watch cancelled.'}` on the js console
     (defn ^:dev/before-load stop! [] (when reactor (reactor)) (set! reactor nil))))
