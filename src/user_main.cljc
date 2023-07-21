(ns user-main
  (:require clojure.edn
            clojure.string
            contrib.data
            contrib.ednish
            contrib.uri ; data_readers 
            [electric-fiddle.api :as App]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.history :as history]
            electric-fiddle.index))

(defn route->path [route] (clojure.string/join "/" (map contrib.ednish/encode-uri route)))
(defn path->route [s]
  (let [s (contrib.ednish/discard-leading-slash s)]
    (case s "" nil (->> (clojure.string/split s #"/") (mapv contrib.ednish/decode-uri)))))

; nested routes don't work yet, check dir explorer etc

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
  )

(e/defn NotFoundPage [] (e/client (dom/h1 (dom/text "Page not found: " (pr-str history/route)))))

(e/defn Index []
  (e/client
    (dom/h1 (dom/text "Index — Electric Fiddle"))
    (e/for [[k _] App/pages]
      (dom/div (history/link [k] (dom/text (name k)))))))

(e/defn Apply [F [a b c :as args]]
  (condp = (count args)
    0 (new F)
    1 (new F a)
    2 (new F a b)
    3 (new F a b c)))

(e/defn Main []
  (binding [history/encode route->path
            history/decode #(or (path->route %) [`Index])]
    (history/router (history/HTML5-History.)
      (set! (.-title js/document) (str (some-> (identity history/route) first name (str " – ")) "Electric Clojure"))
      (binding [dom/node js/document.body
                App/pages electric-fiddle.index/pages]
        (let [[page & args] history/route]
          #_(dom/pre (dom/text (pr-str history/route)))
          (case page 
            `Index (Index.) ; avoid Electric compiler cycle ?
            #_(binding [history/build-route (fn [top-route paths']
                                              (vec (concat top-route #_(butlast top-route) paths')))]) ; page local fiddle links
            #_(history/router 1)
                ; no Electric varadic fns, fix arity
            (new #_Apply. (get App/pages page NotFoundPage) args)))))))
