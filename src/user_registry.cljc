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
            #_[electric-fiddle.index :refer [Index]]
            [hfql-demo.hfql-teeshirt-orders :refer [HFQL-teeshirt-orders]]
            
            electric-demo.demo-two-clocks
            electric-demo.demo-toggle
            electric-demo.demo-system-properties
            electric-demo.demo-chat
            electric-demo.demo-chat-extended
            electric-demo.demo-webview
            electric-demo.demo-todomvc
            electric-demo.demo-todomvc-branched
            electric-demo.demo-todomvc-composed
            electric-demo.demo-explorer
            electric-demo.demo-10k-dom
            electric-demo.demo-svg
            electric-demo.demo-todos-simple
            electric-demo.tutorial-7guis-1-counter
            electric-demo.tutorial-7guis-2-temperature
            electric-demo.tutorial-7guis-4-timer
            electric-demo.tutorial-7guis-5-crud
            electric-demo.demo-virtual-scroll
            electric-demo.demo-color
            electric-demo.demo-tic-tac-toe
            electric-demo.tutorial-blinker
            electric-demo.wip.tag-picker
            electric-demo.wip.demo-custom-types
            electric-demo.wip.tracing
            ; this demo require `npm install`
            #_electric-demo.demo-reagent-interop
            ; these demos require extra deps alias
            #_electric-demo.wip.dennis-exception-leak
            #_electric-demo.wip.demo-stage-ui4
            #_electric-demo.wip.datomic-browser))

; todo: macro to auto-install demos by attaching clj metadata to e/defn vars?
(e/def pages
  {;`Index Index -- don't list in index
   `Fiddle Fiddle
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
   
   ; electric demos
   ;`electric-demo.demo-index/Demos electric-demo.demo-index/Demos
   ;`electric-demo.demo-index/Secrets electric-demo.demo-index/Secrets
   `electric-demo.demo-two-clocks/TwoClocks electric-demo.demo-two-clocks/TwoClocks
   `electric-demo.demo-explorer/DirectoryExplorer electric-demo.demo-explorer/DirectoryExplorer
   ; electric-demo.demo-10k-dom/Dom-10k-Elements electric-demo.demo-10k-dom/Dom-10k-Elements ; todo too slow to unmount, crashes
   `electric-demo.wip.tag-picker/TagPicker electric-demo.wip.tag-picker/TagPicker
   `electric-demo.demo-toggle/Toggle electric-demo.demo-toggle/Toggle
   `electric-demo.wip.demo-custom-types/CustomTypes electric-demo.wip.demo-custom-types/CustomTypes
   `electric-demo.demo-system-properties/SystemProperties electric-demo.demo-system-properties/SystemProperties
   `electric-demo.demo-chat/Chat electric-demo.demo-chat/Chat
   `electric-demo.demo-chat-extended/ChatExtended electric-demo.demo-chat-extended/ChatExtended
   `electric-demo.demo-webview/Webview electric-demo.demo-webview/Webview
   `electric-demo.demo-todos-simple/TodoList electric-demo.demo-todos-simple/TodoList ; css fixes
   `electric-demo.demo-todomvc/TodoMVC electric-demo.demo-todomvc/TodoMVC
   `electric-demo.demo-todomvc-branched/TodoMVCBranched electric-demo.demo-todomvc-branched/TodoMVCBranched
   `electric-demo.demo-todomvc-composed/TodoMVC-composed electric-demo.demo-todomvc-composed/TodoMVC-composed
   `electric-demo.demo-color/Color electric-demo.demo-color/Color
   `electric-demo.demo-virtual-scroll/VirtualScroll electric-demo.demo-virtual-scroll/VirtualScroll
   `electric-demo.tutorial-7guis-1-counter/Counter electric-demo.tutorial-7guis-1-counter/Counter
   `electric-demo.tutorial-7guis-2-temperature/TemperatureConverter electric-demo.tutorial-7guis-2-temperature/TemperatureConverter
   `electric-demo.tutorial-7guis-4-timer/Timer electric-demo.tutorial-7guis-4-timer/Timer
   `electric-demo.tutorial-7guis-5-crud/CRUD electric-demo.tutorial-7guis-5-crud/CRUD
   `electric-demo.demo-tic-tac-toe/TicTacToe electric-demo.demo-tic-tac-toe/TicTacToe
   `electric-demo.demo-svg/SVG electric-demo.demo-svg/SVG
   `electric-demo.tutorial-blinker/Blinker electric-demo.tutorial-blinker/Blinker
   `electric-demo.wip.tracing/TracingDemo electric-demo.wip.tracing/TracingDemo
   ;`electric-demo.demo-reagent-interop/ReagentInterop (when react-available electric-demo.demo-reagent-interop/ReagentInterop)
   ;::demos/dennis-exception-leak wip.dennis-exception-leak/App2
   ;`electric-demo.wip.demo-stage-ui4/CrudForm wip.demo-stage-ui4/CrudForm
   ;`electric-demo.wip.datomic-browser/DatomicBrowser wip.datomic-browser/DatomicBrowser
   
   })
