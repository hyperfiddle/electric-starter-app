(ns electric-tutorial.tutorial
  (:require clojure.string
            [electric-fiddle.fiddle :refer [Fiddle-fn Fiddle-ns]]
            [electric-fiddle.fiddle-markdown :refer [Custom-markdown]]
            [hyperfiddle :as hf]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-svg :as svg]
            [hyperfiddle.router :as r]

            [electric-tutorial.demo-two-clocks :refer [TwoClocks]]
            [electric-tutorial.demo-toggle :refer [Toggle]]
            electric-tutorial.demo-system-properties
            electric-tutorial.demo-chat
            electric-tutorial.tutorial-backpressure
            electric-tutorial.tutorial-lifecycle
            electric-tutorial.demo-chat-extended
            electric-tutorial.demo-webview
            electric-tutorial.demo-todos-simple
            #_electric-tutorial.demo-reagent-interop ; npm install
            electric-tutorial.demo-svg
            electric-tutorial.tutorial-7guis-1-counter
            electric-tutorial.tutorial-7guis-2-temperature
            electric-tutorial.tutorial-7guis-4-timer
            electric-tutorial.tutorial-7guis-5-crud
            ))

(def tutorials
  [["Electric" 
    [`electric-tutorial.demo-two-clocks/TwoClocks
     `electric-tutorial.demo-toggle/Toggle
     `electric-tutorial.demo-system-properties/SystemProperties
     `electric-tutorial.demo-chat/Chat
     `electric-tutorial.tutorial-backpressure/Backpressure
     `electric-tutorial.tutorial-lifecycle/Lifecycle
     ; tutorial-entrypoint
     `electric-tutorial.demo-chat-extended/ChatExtended
     `electric-tutorial.demo-webview/Webview
     `electric-tutorial.demo-todos-simple/TodoList
     #_`electric-tutorial.demo-reagent-interop/ReagentInterop
     `electric-tutorial.demo-svg/SVG

     #_`electric-tutorial.demo-todomvc/TodoMVC
     #_`electric-tutorial.demo-todomvc-composed/TodoMVC-composed
     #_`electric-tutorial.demo-explorer/DirectoryExplorer
     
     #_`electric-demo.demo-virtual-scroll/VirtualScroll ; virtual scroll Server-streamed virtual pagination over node_modules. Check the DOM!
     #_`electric-demo.wip.demo-stage-ui4/CrudForm
     #_`wip.demo-custom-types/CustomTypes ; Custom transit serializers example
     #_`wip.js-interop/QRCode ; Generate QRCodes with a lazily loaded JS library
     ]]
   ["7 GUIs"
    [`electric-tutorial.tutorial-7guis-1-counter/Counter
     `electric-tutorial.tutorial-7guis-2-temperature/TemperatureConverter
     `electric-tutorial.tutorial-7guis-4-timer/Timer
     `electric-tutorial.tutorial-7guis-5-crud/CRUD
     #_`wip.teeshirt-orders/Webview-HFQL ;HFQL hello world. HFQL is a data notation for CRUD apps.
     ]]])

(def tutorials-index (->> tutorials
                       (mapcat (fn [[_group entries]] entries))
                       (map-indexed (fn [idx entry] {::order idx ::id entry}))
                       (contrib.data/index-by ::id)))
(def tutorials-seq (vec (sort-by ::order (vals tutorials-index))))

(defn get-prev-next [page]
  (when-let [order (::order (tutorials-index page))]
    [(get tutorials-seq (dec order))
     (get tutorials-seq (inc order))]))

(defn title [m] (name (::id m)))

(e/defn Nav [page footer?] #_[& [directive alt-text target-s ?wrap :as route]]
  (e/client
    (let [[prev next] (get-prev-next page)]
      #_(println `prev page prev next)
      (dom/div {} (dom/props {:class [(if footer? "user-examples-footer-nav" "user-examples-nav")
                                      (when-not prev "user-examples-nav-start")
                                      (when-not next "user-examples-nav-end")]})
        (when prev
          (r/link [(list (::id prev))]
            (dom/props {:class "user-examples-nav-prev"})
            (dom/text (str "< " (title prev)))))
        (dom/div (dom/props {:class "user-examples-select"})
          (svg/svg (dom/props {:viewBox "0 0 20 20"})
            (svg/path (dom/props {:d "M19 4a1 1 0 01-1 1H2a1 1 0 010-2h16a1 1 0 011 1zm0 6a1 1 0 01-1 1H2a1 1 0 110-2h16a1 1 0 011 1zm-1 7a1 1 0 100-2H2a1 1 0 100 2h16z"})))
          (dom/select
            (e/for [[group-label entries] tutorials]
              (dom/optgroup (dom/props {:label group-label})
                (e/for [id entries]
                  (let [m (tutorials-index id)]
                    (dom/option
                      (dom/props {:value (str id) :selected (= page id)})
                      (dom/text (str (inc (::order m)) ". " (title m))))))))
            (dom/on "change" (e/fn [^js e]
                               (r/Navigate!. [(list (clojure.edn/read-string (.. e -target -value)))])))))
        (when next
          (r/link [(list (::id next))]
            (dom/props {:class "user-examples-nav-next"})
            (dom/text (str (title next) " >"))))))))

(def tutorials2
  {`electric-tutorial.demo-two-clocks/TwoClocks "src-fiddles/electric_tutorial/demo_two_clocks.md"
   `electric-tutorial.demo-toggle/Toggle "src-fiddles/electric_tutorial/demo_toggle.md"
   `electric-tutorial.demo-system-properties/SystemProperties "src-fiddles/electric_tutorial/demo_system_properties.md"
   `electric-tutorial.demo-chat/Chat "src-fiddles/electric_tutorial/demo_chat.md"
   `electric-tutorial.tutorial-backpressure/Backpressure "src-fiddles/electric_tutorial/tutorial_backpressure.md"
   `electric-tutorial.tutorial-lifecycle/Lifecycle "src-fiddles/electric_tutorial/tutorial_lifecycle.md"
   `electric-tutorial.demo-chat-extended/ChatExtended "src-fiddles/electric_tutorial/demo_chat_extended.md"
   `electric-tutorial.demo-webview/Webview "src-fiddles/electric_tutorial/demo_webview.md"
   `electric-tutorial.demo-todos-simple/TodoList "src-fiddles/electric_tutorial/demo_todos_simple.md"
   `electric-tutorial.demo-svg/SVG "src-fiddles/electric_tutorial/demo_svg.md"
   `electric-tutorial.tutorial-7guis-1-counter/Counter "src-fiddles/electric_tutorial/tutorial_7guis_1_counter.md"
   `electric-tutorial.tutorial-7guis-2-temperature/TemperatureConverter "src-fiddles/electric_tutorial/tutorial_7guis_2_temperature.md"
   `electric-tutorial.tutorial-7guis-4-timer/Timer "src-fiddles/electric_tutorial/tutorial_7guis_4_timer.md"
   `electric-tutorial.tutorial-7guis-5-crud/CRUD "src-fiddles/electric_tutorial/tutorial_7guis_5_crud.md"
   ;`electric-tutorial.demo-reagent-interop/ReagentInterop ""
   })

(e/def fiddles
  {`TwoClocks TwoClocks
   `Toggle Toggle
   `electric-tutorial.demo-system-properties/SystemProperties electric-tutorial.demo-system-properties/SystemProperties
   `electric-tutorial.demo-chat/Chat electric-tutorial.demo-chat/Chat
   `electric-tutorial.tutorial-backpressure/Backpressure electric-tutorial.tutorial-backpressure/Backpressure
   `electric-tutorial.tutorial-lifecycle/Lifecycle electric-tutorial.tutorial-lifecycle/Lifecycle
   `electric-tutorial.demo-chat-extended/ChatExtended electric-tutorial.demo-chat-extended/ChatExtended
   `electric-tutorial.demo-webview/Webview electric-tutorial.demo-webview/Webview
   `electric-tutorial.demo-todos-simple/TodoList electric-tutorial.demo-todos-simple/TodoList  ; css fixes
   `electric-tutorial.demo-svg/SVG electric-tutorial.demo-svg/SVG
   `electric-tutorial.tutorial-7guis-1-counter/Counter electric-tutorial.tutorial-7guis-1-counter/Counter
   `electric-tutorial.tutorial-7guis-2-temperature/TemperatureConverter electric-tutorial.tutorial-7guis-2-temperature/TemperatureConverter
   `electric-tutorial.tutorial-7guis-4-timer/Timer electric-tutorial.tutorial-7guis-4-timer/Timer
   `electric-tutorial.tutorial-7guis-5-crud/CRUD electric-tutorial.tutorial-7guis-5-crud/CRUD
   ;`electric-tutorial.demo-reagent-interop/ReagentInterop electric-tutorial.demo-reagent-interop/ReagentInterop
   })

(e/def extensions
  {'fiddle Fiddle-fn
   'fiddle-ns Fiddle-ns})

(e/defn Tutorial []
  (e/client
    (let [[?tutorial] (ffirst r/route)
          ?tutorial   (or ?tutorial `TwoClocks)]
      (binding [hf/pages fiddles]
        (Nav. ?tutorial false)
        (if-some [essay-filename (get tutorials2 ?tutorial)]
          (Custom-markdown. extensions essay-filename)
          (dom/h1 (dom/text "Tutorial not found: " ?tutorial)))
        (Nav. ?tutorial true)))))

