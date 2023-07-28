(ns electric-fiddle.main
  (:require clojure.edn
            clojure.string
            contrib.data
            contrib.ednish
            contrib.uri ; data_readers 
            [electric-fiddle.api :as App]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.history :as history]
            [electric-fiddle.index :refer [Index]]
            user-registry))

(defn route->path [route] (clojure.string/join "/" (map contrib.ednish/encode-uri route)))
(defn path->route [s]
  (let [s (contrib.ednish/discard-leading-slash s)]
    (case s "" nil (->> (clojure.string/split s #"/") (mapv contrib.ednish/decode-uri)))))

(comment
  (clojure.string/split "/user.demo-two-clocks!TwoClocks" #"/")
  (clojure.string/split "user.demo-two-clocks!TwoClocks" #"/")
  (clojure.string/split "" #"/")
  (route->path [`user.demo-two-clocks/TwoClocks]) := "user.demo-two-clocks!TwoClocks"
  (path->route "user.demo-two-clocks!TwoClocks") := [`user.demo-two-clocks/TwoClocks]
  (path->route "/user.demo-two-clocks!TwoClocks") := [`user.demo-two-clocks/TwoClocks]
  (path->route "/user.demo-two-clocks!TwoClocks/") := [`user.demo-two-clocks/TwoClocks]
  (path->route "/user.demo-two-clocks!TwoClocks/foo") := [`user.demo-two-clocks/TwoClocks 'foo]
  (path->route "") := nil
  (path->route nil) := nil)

(e/defn NotFoundPage [args] (e/client (dom/h1 (dom/text "Page not found: " (pr-str history/route)))))

(e/defn Main []
  (binding [history/encode route->path
            history/decode #(or (path->route %) [`Index])]
    (history/router (history/HTML5-History.)
      (set! (.-title js/document) (str (some-> (identity history/route) first name (str " – ")) "Electric Clojure"))
      (binding [dom/node js/document.body
                App/pages user-registry/pages]
        (let [[page & args] history/route]
          (dom/pre (dom/text (pr-str history/route)))
          #_(binding [history/build-route (fn [top-route paths']
                                            (vec (concat top-route #_(butlast top-route) paths')))]) ; page local fiddle links
          (binding [history/build-route (fn [[page :as page-route] local-route]
                                          (println 'page-route page-route 'local-route local-route)
                                          `[~@(case page `Index nil page-route)
                                            ~@local-route])]
            (history/router 1 ; weird, paired with Index ~@
              (case page
                `Index (Index. [])
                (new #_Apply. (get App/pages page NotFoundPage) args))))))))) ; no Electric varadic fns, fix arity
