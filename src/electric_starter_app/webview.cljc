(ns electric-starter-app.webview
  (:require #?(:clj [datascript.core :as d])
            [hyperfiddle.electric-de :as e :refer [$]]
            [hyperfiddle.electric-dom3 :as dom]))

#?(:clj (defonce conn                   ; state survives reload
          (doto (d/create-conn {:order/email {}})
            (d/transact!                ; test data
              [{:order/email "alice@example.com" :order/gender :order/female}
               {:order/email "bob@example.com" :order/gender :order/male}
               {:order/email "charlie@example.com" :order/gender :order/male}]))))

#?(:clj (defn teeshirt-orders [db ?email]
          (sort (d/q '[:find [?e ...]
                       :in $ ?needle :where
                       [?e :order/email ?email]
                       [(clojure.string/includes? ?email ?needle)]]
                  db (or ?email "")))))

(e/defn Teeshirt-orders-view [db]
  (dom/div
    (let [search (dom/input
                   (dom/props {:placeholder "Filter..."})
                   ($ dom/On "input" #(-> % .-target .-value)))]
      (dom/table
        (dom/props {:class "hyperfiddle"})
        (e/cursor [id (e/server (e/diff-by identity ($ e/Offload #(teeshirt-orders db search))))]
          (let [!e (e/server (d/entity db id))]
            (dom/tr
              (dom/td (dom/text id))
              (dom/td (dom/text (e/server (:order/email !e))))
              (dom/td (dom/text (e/server (:order/gender !e)))))))))))

(e/defn Webview []
  ($ Teeshirt-orders-view (e/server (e/watch conn))))
