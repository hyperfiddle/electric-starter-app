(ns user-main
  (:require clojure.edn
            clojure.string
            contrib.data
            contrib.ednish
            contrib.uri ; data_readers
            #?(:clj markdown.core)
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
            user.demo-reagent-interop ; yarn
            wip.demo-stage-ui4))

(e/defn NotFoundPage []
  (e/client (dom/h1 (dom/text "Page not found"))))

; todo: macro to auto-install demos by attaching clj metadata to e/defn vars?

(e/def pages
  {`user.demo-index/Secrets user.demo-index/Secrets
   `user.demo-two-clocks/TwoClocks user.demo-two-clocks/TwoClocks
   `user.demo-toggle/Toggle user.demo-toggle/Toggle
   `user.demo-system-properties/SystemProperties user.demo-system-properties/SystemProperties
   `user.demo-chat/Chat user.demo-chat/Chat
   `user.tutorial-lifecycle/Lifecycle user.tutorial-lifecycle/Lifecycle
   `user.demo-chat-extended/ChatExtended user.demo-chat-extended/ChatExtended
   `user.demo-webview/Webview user.demo-webview/Webview
   `user.demo-todos-simple/TodoList user.demo-todos-simple/TodoList
   `user.demo-reagent-interop/ReagentInterop user.demo-reagent-interop/ReagentInterop
   `wip.demo-stage-ui4/CrudForm wip.demo-stage-ui4/CrudForm
   `user.demo-svg/SVG user.demo-svg/SVG
   ; -- `wip.tracing/TracingDemo wip.tracing/TracingDemo
   `wip.demo-custom-types/CustomTypes wip.demo-custom-types/CustomTypes

   ; 7 GUIs
   `user.tutorial-7guis-1-counter/Counter user.tutorial-7guis-1-counter/Counter
   `user.tutorial-7guis-2-temperature/TemperatureConverter user.tutorial-7guis-2-temperature/TemperatureConverter
   `user.tutorial-7guis-4-timer/Timer user.tutorial-7guis-4-timer/Timer
   `user.tutorial-7guis-5-crud/CRUD user.tutorial-7guis-5-crud/CRUD

   ; Demos
   `user.demo-todomvc/TodoMVC user.demo-todomvc/TodoMVC
   `user.demo-todomvc-composed/TodoMVC-composed user.demo-todomvc-composed/TodoMVC-composed
   `user.demo-explorer/DirectoryExplorer user.demo-explorer/DirectoryExplorer
   ;-- `wip.datomic-browser/DatomicBrowser wip.datomic-browser/DatomicBrowser -- separate repo now, should it come back?
   ; `user.demo-color/Color user.demo-color/Color
   ; -- user.demo-10k-dom/Dom-10k-Elements user.demo-10k-dom/Dom-10k-Elements ; todo too slow to unmount, crashes

   ; Hyperfiddle demos
   `wip.teeshirt-orders/Webview-HFQL wip.teeshirt-orders/Webview-HFQL
   ; `wip.demo-branched-route/RecursiveRouter wip.demo-branched-route/RecursiveRouter
   ; `wip.demo-explorer2/DirectoryExplorer-HFQL wip.demo-explorer2/DirectoryExplorer-HFQL

   ; Hyperfiddle tutorials
   ; `wip.tag-picker/TagPicker wip.tag-picker/TagPicker

   ; Triage
   ; `user.demo-virtual-scroll/VirtualScroll user.demo-virtual-scroll/VirtualScroll
   ; `user.demo-tic-tac-toe/TicTacToe user.demo-tic-tac-toe/TicTacToe   

   ; Tests
   ; ::demos/dennis-exception-leak wip.dennis-exception-leak/App2 
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

(e/defn Code [page]
  (dom/fieldset
   (dom/props {:class "user-examples-code"})
   (dom/legend (dom/text "Code"))
   #_(dom/pre (dom/text (e/server (get-src page)))) 
   (CodeMirror. {:parent dom/node :readonly true} identity identity (e/server (get-src page)))))

(e/defn App [page] 
  (dom/fieldset
   (dom/props {:class (str "user-examples-target " (some-> page name))})
   (dom/legend (dom/text "Result"))
   (e/server (new (get pages page NotFoundPage)))))

(e/defn Markdown [?md-str]
  (e/client
   (let [html (e/server (some-> ?md-str markdown.core/md-to-html-string))]
     (set! (.-innerHTML dom/node) html))))

(e/defn Readme [page]
  (dom/div
   (dom/props {:class "user-examples-readme"})
   (e/server (Markdown. (get-readme page)))))

(def tutorials
  [["Electric" 
    [{::id `user.demo-two-clocks/TwoClocks 
      ::title "Two Clocks – Hello World"
      ::lead "Streaming lexical scope. The server clock is streamed to the client."}
     {::id `user.demo-toggle/Toggle
      ::lead "This demo toggles between client and server with a button."}
     {::id `user.demo-system-properties/SystemProperties
      ::lead "A largrer example of a HTML table backed by a server-side query. Type into the input and see the query update live."} 
     {::id `user.demo-chat/Chat ::lead "A multiplayer chat app. Try two tabs."}
     {::id `user.tutorial-lifecycle/Lifecycle ::lead "mount/unmount component lifecycle"}
     {::id `user.demo-chat-extended/ChatExtended 
      ::lead "Extended chat demo with auth and presence. When multiple sessions are connected, you can see who else is present."}
     {::id `user.demo-webview/Webview 
      ::lead "A database backed webview with reactive updates."}
     {::id `user.demo-todos-simple/TodoList 
      ::lead "minimal todo list. it's multiplayer, try two tabs"} 
     {::id `user.demo-reagent-interop/ReagentInterop
      ::lead "Reagent (React.js) embedded inside Electric. The reactive mouse coordinates cross from Electric to Reagent via props."}
     {::id `user.demo-svg/SVG
      ::lead "SVG support. Note the animation is reactive and driven by javascript cosine."}
     {::id `wip.demo-custom-types/CustomTypes ::lead "Custom transit serializers example"}
     
                                        ; 7 GUIs
     {::id `user.tutorial-7guis-1-counter/Counter ::title "7GUIs Counter" 
      ::lead "See <https://eugenkiss.github.io/7guis/tasks#counter>"}
     {::id `user.tutorial-7guis-2-temperature/TemperatureConverter ::title "7GUIs Temperature Converter"
      ::lead "See <https://eugenkiss.github.io/7guis/tasks#temp>"}
     {::id `user.tutorial-7guis-4-timer/Timer ::title "7GUIs Timer"
      ::lead "See <https://eugenkiss.github.io/7guis/tasks#timer>"}
     {::id `user.tutorial-7guis-5-crud/CRUD ::title "7GUIs CRUD"
      ::lead "See <https://eugenkiss.github.io/7guis/tasks#crud>"}
     
                                        ; Demos
     {::id `user.demo-todomvc/TodoMVC ::demo true ::lead ""}
     {::id `user.demo-todomvc-composed/TodoMVC-composed ::demo true
      ::lead ""}
     {::id `user.demo-explorer/DirectoryExplorer ::demo true
      ::lead "Server-streamed virtual pagination over node_modules. Check the DOM!"} 
     {::id `wip.demo-stage-ui4/CrudForm ::lead "Database-backed CRUD form using Datomic"}
     #_{::id `wip.demo-custom-types/CustomTypes ::lead "Custom transit serializers example"}]]
   ["HFQL"
    [{::id `wip.teeshirt-orders/Webview-HFQL
      ::lead "A teeshirt orders demo with entity relationship constraints"}]]])

(def tutorials-index (contrib.data/index-by ::id tutorials))

(e/defn Nav [page]
  (dom/select
   (dom/props {:class "user-examples-select"})
   (e/for [[group-label entries] tutorials]
     (dom/optgroup (dom/props {:label group-label})
       (e/for [{:keys [::id ::title]} entries]
         (dom/option 
           (dom/props {:value (str id) :selected (= page id)}) 
           (dom/text (or title (name id)))))))
   (dom/on "change" (e/fn [^js e]
                      (history/swap-route! assoc 0 (clojure.edn/read-string (.. e -target -value)))))))

(e/defn Examples []
  (let [[page & [?panel]] history/route]
    (case ?panel
      code (Code. page) ; iframe url for just code
      app (App. page) ; iframe url for just app
      (do
        (dom/h1 (dom/text "Tutorial – Electric Clojure")) 
        (Nav. page)
        (dom/div (dom/props {:class "user-examples-lead"}) 
                 (e/server (Markdown. (::lead (get tutorials-index page)))))
        (App. page)
        (if-not (::demo (get tutorials-index page))
          (Code. page)
          (Code. ""))
        (Readme. page)
        #_(dom/div (dom/props {:class "user-examples-nav"}) 
                   (user.demo-index/Demos.))))))

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
      (set! (.-title js/document) (str #_(clojure.string/capitalize) (some-> (identity history/route) first name) " – Electric Clojure"))
      (binding [dom/node js/document.body]
        (Examples.)))))
