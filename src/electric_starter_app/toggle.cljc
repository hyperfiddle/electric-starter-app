(ns electric-starter-app.toggle
  (:require [hyperfiddle.electric-de :as e :refer [$]]
            [hyperfiddle.electric-dom3 :as dom]))

(e/defn Toggle []
  (let [!x (e/server (atom true)), x (e/server (e/watch !x))]
    (binding [dom/node ($ dom/Root js/document.body)]
      (dom/div
        (dom/text "number type here is: "
                  (case x
                    true (e/client (pr-str (type 1))) ; javascript number type
                    false (e/server (pr-str (type 1)))))) ; java number type

      (dom/div
        (dom/text "current site: " (if x "ClojureScript (client)" "Clojure (server)")))

      (dom/button
        (dom/text "toggle client/server")
        (when-some [off! ($ e/Token ($ dom/On "click"))]
          (off! (e/server (swap! !x not))))))))

#_(e/defn Toggle []
  (let [!x (atom true), x (e/watch !x)]
    (binding [dom/node ($ dom/Root js/document.body)]
      (dom/div
        (dom/text "number type here is: "
                  (case x
                    true (pr-str (type 1)) ; javascript number type
                    false (pr-str (type 1))))) ; java number type

      (dom/div
        (dom/text "current site: " (if x "ClojureScript (client)" "Clojure (server)")))

      (dom/button
        (dom/text "toggle client/server")
        (when-some [off! ($ e/Token ($ dom/On "click"))]
          (off! (swap! !x not)))))))
