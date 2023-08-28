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

(e/defn NotFoundPage [& args] (e/client (dom/h1 (dom/text "Page not found: " (pr-str history/route)))))

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
          (binding [history/build-route (fn [[page :as page-route] [x & xs :as local-route]]
                                          #_(println 'page-route page-route 'local-route local-route)
                                          ; todo - root relative path? need another directive '/
                                          ; allow sideways nav inside same "dir" (app)
                                          (cond 
                                            (= '.. x) `[~@(butlast page-route) ~@(rest local-route)] ; sideways
                                            (= page `Index) local-route
                                            () `[~@page-route ~@local-route]))] ; descend
            (history/router 1 ; weird, paired with Index ~@
              (case page
                `Index (Index.)
                (e/apply (get App/pages page NotFoundPage) args)))))))))
