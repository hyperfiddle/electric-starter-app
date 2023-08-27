(ns electric-tutorial.tutorial
  (:require clojure.string
            [electric-fiddle.api :as App]
            [electric-fiddle.fiddle :refer [Fiddle-fn Fiddle-ns]]
            [electric-fiddle.fiddle-markdown :refer [Custom-markdown]]
            [electric-fiddle.index :refer [Index]]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-svg :as svg]
            [hyperfiddle.history :as history]))

(def tutorials
  [["Electric" 
    [{::id `electric-tutorial.demo-two-clocks/TwoClocks 
      ::title "Two Clocks – Hello World"
      ::lead ""}
     {::id `electric-tutorial.demo-toggle/Toggle
      ::lead "This demo toggles between client and server with a button."}
     {::id `electric-tutorial.demo-system-properties/SystemProperties
      ::lead "A larger example of a HTML table backed by a server-side query. Type into the input and see the query update live."} 
     {::id `electric-tutorial.demo-chat/Chat ::lead "A multiplayer chat app in 30 LOC, all one file. Try two tabs."}
     {::id `electric-tutorial.tutorial-backpressure/Backpressure ::lead "This is just the Two Clocks demo with slight modifications, there is more to learn here."}
     {::id `electric-tutorial.tutorial-lifecycle/Lifecycle ::title "Component Lifecycle" ::lead "mount/unmount component lifecycle"}
     #_{::id 'user ::title "Electric Entrypoint" ::suppress-demo true
        ::lead "This is the Electric entrypoint (in electric-tutorial.cljs). `hyperfiddle.electric/boot` is the Electric compiler entrypoint."}
     {::id `electric-tutorial.demo-chat-extended/ChatExtended 
      ::lead "Extended chat demo with auth and presence. When multiple sessions are connected, you can see who else is present."}
     {::id `electric-tutorial.demo-webview/Webview 
      ::lead "A database backed webview with reactive updates."}
     {::id `electric-tutorial.demo-todos-simple/TodoList 
      ::lead "minimal todo list. it's multiplayer, try two tabs"} 
     {::id `electric-tutorial.demo-reagent-interop/ReagentInterop
      ::lead "Reagent (React.js) embedded inside Electric. The reactive mouse coordinates cross from Electric to Reagent via props."}
     {::id `electric-tutorial.demo-svg/SVG
      ::lead "SVG support. Note the animation is reactive and driven by javascript cosine."}

                                        ; 7 GUIs
     {::id `electric-tutorial.tutorial-7guis-1-counter/Counter ::title "7GUIs Counter" 
      ::lead "See <https://eugenkiss.github.io/7guis/tasks#counter>"}
     {::id `electric-tutorial.tutorial-7guis-2-temperature/TemperatureConverter ::title "7GUIs Temperature Converter"
      ::lead "See <https://eugenkiss.github.io/7guis/tasks#temp>"}
     {::id `electric-tutorial.tutorial-7guis-4-timer/Timer ::title "7GUIs Timer"
      ::lead "See <https://eugenkiss.github.io/7guis/tasks#timer>"}
     {::id `electric-tutorial.tutorial-7guis-5-crud/CRUD ::title "7GUIs CRUD"
      ::lead "See <https://eugenkiss.github.io/7guis/tasks#crud>"}
     
                                        ; Demos
    #_{::id `electric-tutorial.demo-todomvc/TodoMVC ::suppress-code true  ::lead "TodoMVC as a function"}
    #_{::id `electric-tutorial.demo-todomvc-composed/TodoMVC-composed ::suppress-code true ::lead "Demo of app composition by putting a whole fullstack app inside a for loop."}
    #_{::id `electric-tutorial.demo-explorer/DirectoryExplorer ::suppress-code true ::lead "Server-streamed virtual pagination over node_modules. Check the DOM!"} 
    ;;  #_{::id `wip.demo-stage-ui4/CrudForm ::lead "Database-backed CRUD form using Datomic"}
    ;;  {::id `wip.demo-custom-types/CustomTypes ::lead "Custom transit serializers example"}
    #_{::id `wip.js-interop/QRCode ::lead "Generate QRCodes with a lazily loaded JS library"}
     ]]
   #_["HFQL"
    [{::id `wip.teeshirt-orders/Webview-HFQL
      ::lead "HFQL hello world. HFQL is a data notation for CRUD apps."}]]])

(def tutorials-index (->> tutorials
                       (mapcat (fn [[_group entries]] entries))
                       (map-indexed (fn [idx entry] (assoc entry ::order idx)))
                       (contrib.data/index-by ::id)))
(def tutorials-seq (vec (sort-by ::order (vals tutorials-index))))

(defn get-prev-next [page]
  (when-let [order (::order (tutorials-index page))]
    [(get tutorials-seq (dec order))
     (get tutorials-seq (inc order))]))

(defn title [{:keys [::id ::title]}] (or title (some-> id name))) ; busted some->

(e/defn Nav [page footer?] #_[& [directive alt-text target-s ?wrap :as route]]
  (let [[prev next] (get-prev-next page)]
    #_(println `prev page prev next)
    (dom/div {} (dom/props {:class [(if footer? "user-examples-footer-nav" "user-examples-nav")
                                    (when-not prev "user-examples-nav-start")
                                    (when-not next "user-examples-nav-end")]})
      (when prev
        (history/link [(::id prev)] 
          (dom/props {:class "user-examples-nav-prev"})
          (dom/text (str "< " (title prev)))))
      (dom/div (dom/props {:class "user-examples-select"})
        (svg/svg (dom/props {:viewBox "0 0 20 20"})
          (svg/path (dom/props {:d "M19 4a1 1 0 01-1 1H2a1 1 0 010-2h16a1 1 0 011 1zm0 6a1 1 0 01-1 1H2a1 1 0 110-2h16a1 1 0 011 1zm-1 7a1 1 0 100-2H2a1 1 0 100 2h16z"})))
        (dom/select
          (e/for [[group-label entries] tutorials]
            (dom/optgroup (dom/props {:label group-label})
              (e/for [{:keys [::id]} entries]
                (let [entry (tutorials-index id)]
                  (dom/option
                    (dom/props {:value (str id) :selected (= page id)})
                    (dom/text (str (inc (::order entry)) ". " (title entry))))))))
          (dom/on "change" (e/fn [^js e]
                             (history/navigate! history/!history [(clojure.edn/read-string (.. e -target -value))])))))
      (when next
        (history/link [(::id next)] 
          (dom/props {:class "user-examples-nav-next"})
          (dom/text (str (title next) " >")))))))

(def tutorials2
  {`electric-tutorial.demo-two-clocks/TwoClocks "src/electric_tutorial/demo_two_clocks.md"})

(e/def extensions
  {'fiddle Fiddle-fn
   'fiddle-ns Fiddle-ns})

(e/defn Tutorial [& [?tutorial :as route]]
  (if (nil? (seq route)) (binding [App/pages tutorials2] (Index.))
    (do #_(dom/pre (dom/text (pr-str history/route)))
      #_ (e/client (dom/div #_(dom/props {:class ""}))) ; fix css grid next
      (Nav. ?tutorial false)
      (let [essay-filename (get tutorials2 ?tutorial)]
        (cond
          (nil? ?tutorial) (binding [App/pages tutorials] (Index.))
          (nil? essay-filename) (dom/h1 (dom/text "Tutorial not found: " history/route))
          () (Custom-markdown. extensions essay-filename)))
      (Nav. ?tutorial true))))