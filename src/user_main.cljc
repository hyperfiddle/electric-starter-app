(ns user-main
  (:require contrib.uri ; data_readers
            contrib.ednish
            clojure.string
            #?(:clj clarktown.core)
            [contrib.electric-codemirror :refer [CodeMirror]]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.history :as history]
            user.demo-index

            user.demo-two-clocks
            user.demo-toggle
            user.demo-system-properties
            user.demo-chat
            user.demo-chat-extended
            user.demo-webview
            user.demo-todomvc
            user.demo-todomvc-composed

            user.demo-explorer
            wip.demo-explorer2
            user.demo-10k-dom
            user.demo-svg
            user.demo-todos-simple
            user.tutorial-7guis-1-counter
            user.tutorial-7guis-2-temperature
            user.tutorial-7guis-4-timer
            user.tutorial-7guis-5-crud
            user.demo-virtual-scroll
            user.demo-color
            user.demo-tic-tac-toe
            user.tutorial-lifecycle
            wip.demo-branched-route
            #_wip.hfql
            wip.tag-picker
            wip.teeshirt-orders
            wip.demo-custom-types
            wip.tracing

            ; this demo require `npm install`
            #_user.demo-reagent-interop

            ; these demos require extra deps alias
            #_wip.dennis-exception-leak
            #_wip.demo-stage-ui4
            #_wip.datomic-browser
            ))

(e/defn NotFoundPage []
  (e/client (dom/h1 (dom/text "Page not found"))))

; todo: macro to auto-install demos by attaching clj metadata to e/defn vars?

(e/def pages
  {`user.demo-index/Secrets user.demo-index/Secrets
   `user.demo-two-clocks/TwoClocks user.demo-two-clocks/TwoClocks
   ;`wip.teeshirt-orders/Webview-HFQL wip.teeshirt-orders/Webview-HFQL
   `user.demo-explorer/DirectoryExplorer user.demo-explorer/DirectoryExplorer
   ;`wip.demo-explorer2/DirectoryExplorer-HFQL wip.demo-explorer2/DirectoryExplorer-HFQL
   ;user.demo-10k-dom/Dom-10k-Elements user.demo-10k-dom/Dom-10k-Elements ; todo too slow to unmount, crashes
   `wip.demo-branched-route/RecursiveRouter wip.demo-branched-route/RecursiveRouter
   `wip.tag-picker/TagPicker wip.tag-picker/TagPicker
   `user.demo-toggle/Toggle user.demo-toggle/Toggle
   `wip.demo-custom-types/CustomTypes wip.demo-custom-types/CustomTypes
   `user.demo-system-properties/SystemProperties user.demo-system-properties/SystemProperties
   `user.demo-chat/Chat user.demo-chat/Chat
   `user.demo-chat-extended/ChatExtended user.demo-chat-extended/ChatExtended
   `user.demo-webview/Webview user.demo-webview/Webview
   `user.demo-todos-simple/TodoList user.demo-todos-simple/TodoList ; css fixes
   `user.demo-todomvc/TodoMVC user.demo-todomvc/TodoMVC
   `user.demo-todomvc-composed/TodoMVC-composed user.demo-todomvc-composed/TodoMVC-composed
   `user.demo-color/Color user.demo-color/Color
   `user.demo-virtual-scroll/VirtualScroll user.demo-virtual-scroll/VirtualScroll
   `user.tutorial-7guis-1-counter/Counter user.tutorial-7guis-1-counter/Counter
   `user.tutorial-7guis-2-temperature/TemperatureConverter user.tutorial-7guis-2-temperature/TemperatureConverter
   `user.tutorial-7guis-4-timer/Timer user.tutorial-7guis-4-timer/Timer
   `user.tutorial-7guis-5-crud/CRUD user.tutorial-7guis-5-crud/CRUD
   `user.demo-tic-tac-toe/TicTacToe user.demo-tic-tac-toe/TicTacToe
   `user.demo-svg/SVG user.demo-svg/SVG
   `user.tutorial-lifecycle/Lifecycle user.tutorial-lifecycle/Lifecycle
   `wip.tracing/TracingDemo wip.tracing/TracingDemo
   ;`user.demo-reagent-interop/ReagentInterop user.demo-reagent-interop/ReagentInterop
   ;::demos/dennis-exception-leak wip.dennis-exception-leak/App2
   ;`wip.demo-stage-ui4/CrudForm wip.demo-stage-ui4/CrudForm
   ;`wip.datomic-browser/DatomicBrowser wip.datomic-browser/DatomicBrowser
   })

#?(:clj (defn get-src [qualified-sym]
          (try (-> (ns-resolve *ns* qualified-sym) meta :file
                 (->> (str "src/")) slurp)
               (catch java.io.FileNotFoundException _))))

#?(:clj (defn get-readme [qualified-sym]
          (try (-> (ns-resolve *ns* qualified-sym) meta :file
                 (clojure.string/split #"\.cljc") first (str ".md")
                 (->> (str "src/")) slurp)
               (catch java.io.FileNotFoundException _))))

(comment
  (get-src `user.demo-two-clocks/TwoClocks)
  (get-readme `user.demo-two-clocks/TwoClocks))

(e/defn Code [code] (CodeMirror. {:parent dom/node :readonly true} identity identity code))
(e/defn App [page] (e/server (new (get pages page NotFoundPage))))

(e/defn Examples []
  (let [[page & [?panel]] history/route]
    (case ?panel
      code (Code. page) ; iframe url for just code
      app (App. page) ; iframe url for just app
      (do
        (dom/link (dom/props {:rel :stylesheet, :href "/user/examples.css"}))
        #_(dom/pre (dom/text (contrib.str/pprint-str history/route))) ; debug

        (dom/div (dom/props {:class "user-examples"})
          (dom/h1 (dom/text (name page) " — Electric Clojure tutorial"))
          (dom/fieldset (dom/legend (dom/text "Result"))
            (dom/props {:class ["user-examples-target" (name page)]})
            (App. page))
          (dom/fieldset (dom/legend (dom/text "Code"))
            (dom/props {:class "user-examples-code"})
            (Code. (e/server (get-src page))))
          (dom/div (dom/props {:class "user-examples-readme"})
            (let [html (e/server (some-> (get-readme page) clarktown.core/render))]
              (set! (.-innerHTML dom/node) html)))
          (dom/div (dom/props {:class "user-examples-nav"})
            (user.demo-index/Demos.)))))))

(defn route->path [x] (->> x (map contrib.ednish/encode-uri) (interpose "/") (apply str)))
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

(e/defn Main []
  (binding [history/encode route->path
            history/decode #(or (path->route %) [`user.demo-two-clocks/TwoClocks])]
    (history/router (history/HTML5-History.)
      (set! (.-title js/document) (str (clojure.string/capitalize (some-> (identity history/route) first name)) " - Hyperfiddle"))
      (binding [dom/node js/document.body]
        (Examples.)))))
