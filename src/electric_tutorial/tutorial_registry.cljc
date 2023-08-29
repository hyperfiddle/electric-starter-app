(ns electric-tutorial.tutorial-registry
  (:require [hyperfiddle.electric :as e] 
  
            [electric-tutorial.tutorial :refer [Tutorial]]
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
            electric-tutorial.tutorial-7guis-5-crud))

; todo: macro to auto-install demos by attaching clj metadata to e/defn vars?
(e/def pages
  {`Tutorial Tutorial
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
   })