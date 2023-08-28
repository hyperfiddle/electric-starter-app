(ns electric-tutorial.demo-reagent-interop
  #?(:cljs (:require-macros [electric-tutorial.demo-reagent-interop :refer [with-reagent]]))
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            #?(:cljs [reagent.core :as r])
            #?(:cljs ["recharts" :refer  [ScatterChart Scatter LineChart Line
                                          XAxis YAxis CartesianGrid]])
            #?(:cljs ["react-dom/client" :as ReactDom])))

#?(:cljs (def ReactRootWrapper
           (r/create-class
            {:component-did-mount (fn [this] (js/console.log "mounted"))
             :render (fn [this]
                       (let [[_ Component & args] (r/argv this)]
                         (into [Component] args)))})))

#?(:cljs (defn create-root
           "See https://reactjs.org/docs/react-dom-client.html#createroot"
           ([node] (create-root node (str (gensym))))
           ([node id-prefix]
            (ReactDom/createRoot node #js {:identifierPrefix id-prefix}))))

#?(:cljs (defn render [root & args]
           (.render root (r/as-element (into [ReactRootWrapper] args)))))

(defmacro with-reagent [& args]
  `(dom/div  ; React will hijack this element and empty it.
     (let [root# (create-root dom/node)]
       (render root# ~@args)
       (e/on-unmount #(.unmount root#)))))

;; Reagent World
(defn TinyLineChart [data]
  #?(:cljs
     [:> LineChart {:width 400 :height 200 :data (clj->js data)}
      [:> CartesianGrid {:strokeDasharray "3 3"}]
      [:> XAxis {:dataKey "name"}]
      [:> YAxis]
      [:> Line {:type "monotone", :dataKey "pv", :stroke "#8884d8", :strokeWidth 2}]
      [:> Line {:type "monotone", :dataKey "uv", :stroke "#82ca9d", :strokeWidth 2}]]))

(defn MousePosition [x y]
  #?(:cljs
     [:> ScatterChart {:width 300 :height 300 
                       :margin #js{:top 20, :right 20, :bottom 20, :left 20}}
      [:> CartesianGrid {:strokeDasharray "3 3"}]
      [:> XAxis {:type "number", :dataKey "x", :unit "px", :domain #js[0 2000]}]
      [:> YAxis {:type "number", :dataKey "y", :unit "px", :domain #js[0 2000]}]
      [:> Scatter {:name "Mouse position",
                   :data (clj->js [{:x x, :y y}]), :fill "#8884d8"}]]))

;; Electric Clojure

(e/defn ReagentInterop []
  (e/client
    (let [[x y] (dom/on! js/document "mousemove"
                         (fn [e] [(.-clientX e) (.-clientY e)]))]
      (with-reagent MousePosition x y) ; reactive
      ;; Adapted from https://recharts.org/en-US/examples/TinyLineChart
      (with-reagent TinyLineChart 
        [{:name "Page A" :uv 4000 :amt 2400 :pv 2400}
         {:name "Page B" :uv 3000 :amt 2210 :pv 1398}
         {:name "Page C" :uv 2000 :amt 2290 :pv (+ 6000 (* -5 y))} ; reactive
         {:name "Page D" :uv 2780 :amt 2000 :pv 3908}
         {:name "Page E" :uv 1890 :amt 2181 :pv 4800}
         {:name "Page F" :uv 2390 :amt 2500 :pv 3800}
         {:name "Page G" :uv 3490 :amt 2100 :pv 4300}]))))