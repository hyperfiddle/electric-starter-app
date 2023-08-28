(ns user-registry
  (:require [hyperfiddle.electric :as e]
            
            ; fiddle database, todo dynamic
            [dustingetz.demo-explorer-hfql :refer [DirectoryExplorer-HFQL]]
            [dustingetz.y2023.bug-unmount :refer [Bug-unmount]]
            [dustingetz.hfql-intro :refer [HFQL-demo-wrap
                                           Teeshirt-orders-1
                                           Teeshirt-orders-2
                                           Teeshirt-orders-3
                                           Teeshirt-orders-4
                                           Teeshirt-orders-5]]
            [dustingetz.y-fib :refer [Y-fib]]
            [dustingetz.y-dir :refer [Y-dir]]
            [electric-fiddle.essay :refer [Essay]]
            [electric-fiddle.fiddle :refer [Fiddle]]
            [hfql-demo.hfql-teeshirt-orders :refer [HFQL-teeshirt-orders]]

            [electric-tutorial.tutorial :refer [Tutorial]]
            [electric-tutorial.demo-two-clocks :refer [TwoClocks]]
            [electric-tutorial.demo-toggle :refer [Toggle]] #_electric-demo.demo-toggle
            electric-tutorial.demo-system-properties #_electric-demo.demo-system-properties
            electric-tutorial.demo-chat #_electric-demo.demo-chat
            electric-tutorial.tutorial-backpressure
            electric-tutorial.tutorial-lifecycle
            electric-tutorial.demo-chat-extended #_electric-demo.demo-chat-extended
            electric-tutorial.demo-webview
            electric-tutorial.demo-todos-simple #_electric-demo.demo-todos-simple
            #_electric-tutorial.demo-reagent-interop #_electric-demo.demo-reagent-interop ; yarn
            electric-tutorial.demo-svg #_electric-demo.demo-svg
            electric-tutorial.tutorial-7guis-1-counter #_electric-demo.tutorial-7guis-1-counter
            electric-tutorial.tutorial-7guis-2-temperature #_electric-demo.tutorial-7guis-2-temperature
            electric-tutorial.tutorial-7guis-4-timer #_electric-demo.tutorial-7guis-4-timer
            electric-tutorial.tutorial-7guis-5-crud #_electric-demo.tutorial-7guis-5-crud

            electric-demo.demo-todomvc #_electric-tutorial.demo-todomvc
            electric-demo.demo-todomvc-branched
            electric-demo.demo-todomvc-composed #_electric-tutorial.demo-todomvc-composed
            electric-demo.demo-explorer
            electric-demo.demo-10k-dom
            electric-demo.demo-virtual-scroll
            electric-demo.demo-color
            electric-demo.demo-tic-tac-toe
            electric-demo.wip.tag-picker
            electric-demo.wip.demo-custom-types
            electric-demo.wip.tracing
            electric-demo.wip.js-interop
            #_electric-demo.wip.dennis-exception-leak ; these demos require extra deps alias
            #_electric-demo.wip.demo-stage-ui4
            #_electric-demo.wip.datomic-browser))

; todo: macro to auto-install demos by attaching clj metadata to e/defn vars?
(e/def pages
  {`Fiddle Fiddle
   `Y-fib Y-fib
   `Y-dir Y-dir
   `Essay Essay
   `HFQL-demo-wrap HFQL-demo-wrap
   `HFQL-teeshirt-orders HFQL-teeshirt-orders
   `Teeshirt-orders-1 Teeshirt-orders-1
   `Teeshirt-orders-2 Teeshirt-orders-2
   `Teeshirt-orders-3 Teeshirt-orders-3
   `Teeshirt-orders-4 Teeshirt-orders-4
   `Teeshirt-orders-5 Teeshirt-orders-5 
   `DirectoryExplorer-HFQL DirectoryExplorer-HFQL
   `Bug-unmount Bug-unmount
   
   `Tutorial Tutorial
   `TwoClocks TwoClocks
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
   
  ;;  `electric-demo.demo-explorer/DirectoryExplorer electric-demo.demo-explorer/DirectoryExplorer 
  ;;  `electric-demo.wip.tag-picker/TagPicker electric-demo.wip.tag-picker/TagPicker
  ;;  `electric-demo.wip.demo-custom-types/CustomTypes electric-demo.wip.demo-custom-types/CustomTypes
  ;;  `electric-demo.demo-todomvc/TodoMVC electric-demo.demo-todomvc/TodoMVC
  ;;  `electric-demo.demo-todomvc-branched/TodoMVCBranched electric-demo.demo-todomvc-branched/TodoMVCBranched
  ;;  `electric-demo.demo-todomvc-composed/TodoMVC-composed electric-demo.demo-todomvc-composed/TodoMVC-composed 
  ;;  `electric-demo.demo-color/Color electric-demo.demo-color/Color
  ;;  `electric-demo.demo-virtual-scroll/VirtualScroll electric-demo.demo-virtual-scroll/VirtualScroll
  ;;  `electric-demo.demo-tic-tac-toe/TicTacToe electric-demo.demo-tic-tac-toe/TicTacToe
  ;;  `electric-demo.wip.tracing/TracingDemo electric-demo.wip.tracing/TracingDemo
   
   ;`electric-demo.demo-10k-dom/Dom-10k-Elements electric-demo.demo-10k-dom/Dom-10k-Elements ; todo too slow to unmount, crashes
   ;`electric-demo.wip.js-interop/QRCode electric-demo.wip.js-interop/QRCode -- Unable to resolve symbol: js/QRCode
   ;`electric-demo.demo-reagent-interop/ReagentInterop (when react-available electric-demo.demo-reagent-interop/ReagentInterop)
   ;`electric-demo.wip.demo-stage-ui4/CrudForm wip.demo-stage-ui4/CrudForm
   ;`electric-demo.wip.datomic-browser/DatomicBrowser wip.datomic-browser/DatomicBrowser
   ;`wip.datomic-browser/DatomicBrowser wip.datomic-browser/DatomicBrowser -- separate repo now, should it come back?
   ;`wip.demo-branched-route/RecursiveRouter wip.demo-branched-route/RecursiveRouter
   })