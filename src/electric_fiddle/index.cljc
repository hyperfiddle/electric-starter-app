(ns electric-fiddle.index
  (:require clojure.string
            [electric-fiddle.api :as App]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.history :as history]))

(e/defn Index [route] ; duplicated from user-main/Index to avoid cycle in Electric compiler
  (e/client
    (dom/h1 (dom/text `Index))
    #_(dom/pre (dom/text (pr-str history/route)))
    #_(binding [history/build-route (fn [top-route paths'] (vec (concat (butlast top-route) paths')))])
    (e/for [[k _] App/pages]
      (let [href (vec (concat
                        (cond
                          (= history/route [`Index]) nil
                          () history/route)
                        [k]))]
        (dom/div (history/link href
                   (dom/text (name k))
                   #_(dom/text " " (history/build-route history/history href))))))))
