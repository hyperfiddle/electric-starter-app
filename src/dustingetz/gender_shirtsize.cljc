(ns dustingetz.gender-shirtsize
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

(declare orders genders shirt-sizes)

(e/defn Teeshirt-orders-1 [_]
  (e/client 
    (dom/table
      (e/server
        (e/for-by :db/id [record (orders "")]
          (let [{:keys [db/id 
                        order/email 
                        order/gender
                        order/shirt-size]} record]
            (e/client
              (dom/tr
                (dom/td (dom/input id))
                (dom/td (dom/input email))
                (dom/td (dom/select
                          :value gender
                          :options (e/fn [filter] 
                                     (e/server (genders filter)))))
                (dom/td (dom/select
                          :value shirt-size
                          :options (e/fn [filter] 
                                     (e/server (shirt-sizes gender filter)))))))))))))


(defn Teeshirt-orders-1 [_]
  (let [filter (dom/input)]
    (dom/table
      (for [{:keys [db/id
                    order/email
                    order/gender
                    order/shirt-size]} (orders filter)]
        (dom/tr
          (dom/td (dom/input id))
          (dom/td (dom/input email))
          (dom/td (dom/select
                    :value gender
                    :options (fn [filter]
                               (genders filter))))
          (dom/td (dom/select
                    :value shirt-size
                    :options (fn [filter]
                               (shirt-sizes gender filter)))))))))