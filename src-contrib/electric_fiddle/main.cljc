(ns electric-fiddle.main
  (:require
   [hyperfiddle :as hf]
   [hyperfiddle.electric :as e]
   [hyperfiddle.electric-dom2 :as dom]
   [hyperfiddle.router :as r]
   [electric-fiddle.index :refer [Index]]
   ))

(e/defn NotFoundPage [& args] (e/client (dom/h1 (dom/text "Page not found: " (pr-str r/route)))))

(e/defn RedirectLegacyLinks! [link]
  ;; Keep existing links working.
  ;; Demos used to be identified by their fully qualified name - e.g. `hello-fiddle.fiddles/Hello
  ;; They are now represented by an s-expression - e.g. `(electric-tutorial.demo-color/Color h s l)
  (if (or (seq? link) (vector? link))
    link
    (do (r/Navigate!. [(list link)])
        nil)))

(e/defn Main [ring-req]
  (e/server
    (binding [e/http-request ring-req]
      (e/client
        (binding [dom/node js/document.body]
          (r/router (r/HTML5-History.)
            (dom/pre (dom/text (pr-str r/route)))
            (let [route       (or (ffirst r/route) `(Index)) ; route looks like {(f args) nil} or nil
                  [f & args] (RedirectLegacyLinks!. route)]
              (set! (.-title js/document) (str (some-> f name (str " – ")) "Electric Fiddle"))
              (case f
                `Index (Index.)
                (r/focus [route]
                  (e/apply (get hf/pages f NotFoundPage) args))))))))))

