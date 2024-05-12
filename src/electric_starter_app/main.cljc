(ns electric-starter-app.main
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

;; Saving this file will automatically recompile and update in your browser

(e/def tests)
(e/def assertions)

(e/defn Is [desc assertion] (swap! assertions conj [(random-uuid) desc assertion]))
(defmacro is [desc assertion] `(new Is ~desc ~assertion))

(e/defn Testing [desc Body]
  (e/client
    (binding [assertions (atom [])]
      (swap! tests conj [(random-uuid) desc assertions])
      (new Body))))

(defmacro testing [desc & body] `(new Testing ~desc (e/fn [] ~@body)))
(defmacro with-testing-scope [& body] `(binding [tests (atom [])] ~@body))

(e/defn RenderIs [desc assertion]
  (e/client
    (dom/div
      (dom/span (dom/text (if assertion "🟢" "🔴")) (dom/style {:margin-right "0.5rem"}))
      (dom/text desc))))

(e/defn RenderTests []
  (e/client
    (dom/div (dom/props {:class "hf-tests"})
      (e/for-by first [[_ desc assertions] (e/watch tests)]
        (dom/div
          (dom/props {:class "hf-test"})
          (dom/style {:border "1px solid"})
          (dom/h3 (dom/text "Test case: " desc))
          (dom/ul
            (e/for-by first [[_ desc assertion] (e/watch assertions)]
              (new RenderIs desc assertion))))))))

(e/defn Main [ring-request]
  (e/client
    (with-testing-scope
      (binding [dom/node js/document.body]
        (dom/h1 (dom/text "Hello from Electric Clojure"))
        (dom/p (dom/text "Source code for this page is in ")
          (dom/code (dom/text "src/electric_start_app/main.cljc")))
        (dom/p
          (dom/text "Make sure you check the ")
          (dom/a
            (dom/props {:href "https://electric.hyperfiddle.net/" :target "_blank"})
            (testing "electric hyperlink"
              (is "target is \"_blank\"" (= "_blank" (.-target dom/node)))
              (is "href is \"hyperfiddle\"" (= "hyperfiddle" (.-href dom/node))))
            (dom/text "Electric Tutorial")))
        (new RenderTests)))))
