(ns user.demo-index
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.history :as router])) ; for link only

(def pages
  [`user.demo-two-clocks/TwoClocks
   `user.demo-toggle/Toggle
   `user.demo-system-properties/SystemProperties
   `user.demo-webview/Webview
   `user.demo-chat/Chat
   `user.tutorial-lifecycle/Lifecycle
   `user.demo-chat-extended/ChatExtended
   `user.demo-todos-simple/TodoList
   `user.demo-reagent-interop/ReagentInterop
   `user.demo-svg/SVG])

(def seven-guis
  [`user.tutorial-7guis-1-counter/Counter
   `user.tutorial-7guis-2-temperature/TemperatureConverter
   `user.tutorial-7guis-4-timer/Timer
   `user.tutorial-7guis-5-crud/CRUD])

(def demos
  [`user.demo-explorer/DirectoryExplorer
   `user.demo-todomvc/TodoMVC
   `user.demo-todomvc-composed/TodoMVC-composed
   `user.demo-color/Color])

(def secret-pages
  [`wip.teeshirt-orders/Webview-HFQL
   `wip.demo-explorer2/DirectoryExplorer-HFQL
   ;`user.demo-10k-dom/Dom-10k-Elements
   `wip.demo-branched-route/RecursiveRouter
   `wip.tag-picker/TagPicker
   `wip.demo-custom-types/CustomTypes
   `wip.tracing/TracingDemo
   ;`user.demo-tic-tac-toe/TicTacToe
   ;`user.demo-virtual-scroll/VirtualScroll

   ; need extra deps alias
   ;::dennis-exception-leak
   #_`wip.demo-stage-ui4/CrudForm
   #_`wip.datomic-browser/DatomicBrowser])

(e/defn Demos []
  (e/client
    (dom/h3 (dom/text "Tutorial"))
    (e/for [k pages]
      (dom/div (router/link [k] (dom/text (name k)))))

    (dom/h3 (dom/text "7 GUIs"))
    (e/for [k seven-guis]
      (dom/div (router/link [k] (dom/text (name k)))))

    (dom/h3 (dom/text "Demos")) ; no source code here, can link to GH
    (e/for [k demos]
      (dom/div (router/link [k] (dom/text (name k)))))

    (dom/div (dom/style {:opacity 0})
      (router/link [`Secrets] (dom/text "secret-hyperfiddle-demos")))))

(e/defn Secrets []
  (e/client
    (dom/h1 (dom/text "Wip unpublished demos (unstable/wip)")
      (dom/comment_ "ssh" "it's a secret"))
    (dom/p "Some require a database connection and are often broken.")
    (e/for [k secret-pages]
      (dom/div (router/link [k] (dom/text (name k)))))))
