(ns electric-tutorial.demo-color
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.router :as r]
            [contrib.color :as c]))

;; Goal is to demonstrate:
;; - fine-grained reactivity on CSS properties
;; - Non-trivial DOM api usage (canvas)

(def CANVAS-WIDTH 360)                  ; px
(def CANVAS-HEIGHT 100)                 ; px

(defn format-rgb [[r g b]] (str "rgb("r","g","b")"))

#?(:cljs
   (defn draw! [^js canvas colorf]
     (let [ctx (.getContext canvas "2d")]
       (loop [angle 0]
         (set! (.-strokeStyle ctx) (colorf angle))
         (.beginPath ctx)
         (.moveTo ctx angle 0)
         (.lineTo ctx angle CANVAS-HEIGHT)
         (.closePath ctx)
         (.stroke ctx)
         (when (< angle 360)
           (recur (inc angle)))))))

#?(:cljs
   (defn draw-gradient! [canvas hue colorf]
     (draw! canvas (fn [angle] (format-rgb (if (= angle hue) [255 255 255] (colorf angle)))))))

(defn saturation->chroma [saturation] (* 0.158 (/ saturation 100)))

(e/defn Tile [color]
  (e/client
    (dom/div (dom/props {:style {:display          :flex
                                 :align-items      :center
                                 :justify-content  :center
                                 :color            :white
                                 :background-color (format-rgb color)
                                 :width            "100px"
                                 :height           "100%"
                                 }})
      (dom/text "Contrast"))))

(e/defn Color [& [h s l]]
  (e/client
    (let [h (or h 180)
          s (or s 80)
          l (or l 70)
          SetRoute! (e/fn [h s l] (r/ReplaceState!. ['.. `(Color ~h ~s ~l)]))]
      (dom/div (dom/props {:style {:display               :grid
                                   :grid-template-columns "auto 1fr auto"
                                   :gap                   "0 1rem"
                                   :align-items           :center
                                   :justify-items         :stretch
                                   :max-width             "600px"}})
               (dom/p (dom/text "Lightness"))
               (dom/input (dom/props {:type  :range
                                      :min   0
                                      :max   100
                                      :step  1
                                      :value l})
                          (dom/on "input" (e/fn [^js e] (SetRoute!. h s (js/parseInt (.. e -target -value))))))
               (dom/p (dom/text l "%"))

               (dom/p (dom/text "Saturation"))
               (dom/input (dom/props {:type  :range
                                      :min   0
                                      :max   100
                                      :step  1
                                      :value s})
                          (dom/on "input" (e/fn [^js e] (SetRoute!. h (js/parseInt (.. e -target -value)) l))))
               (dom/p (dom/text s "%"))


               (dom/p (dom/text "Hue"))
               (dom/input (dom/props {:type  :range
                                      :min   0
                                      :max   360
                                      :step  1
                                      :value h})
                          (dom/on "input" (e/fn [^js e] (SetRoute!. (js/parseInt (.. e -target -value)) s l))))
               (dom/p (dom/text h "°"))


               (dom/p (dom/text "HSL"))
               (dom/canvas (dom/props {:width  360
                                       :height 100})
                           (draw-gradient! dom/node h (fn [h] (c/hsl->rgb [h s l])))
                           )
               (Tile. (c/hsl->rgb [h s l]))

               (dom/p (dom/text "OKLCH"))
               (dom/canvas (dom/props {:width  360
                                       :height 100})
                           (draw-gradient! dom/node h (fn [h] (c/oklch->rgb [l (saturation->chroma s) h]))))
               (Tile. (c/oklch->rgb [l (saturation->chroma s) h]))

               (dom/p (dom/text "HSLuv"))
               (dom/canvas (dom/props {:width  360
                                       :height 100})
                           (draw-gradient! dom/node h (fn [h] (c/hsluv->rgb [h s l]))))
               (Tile. (c/hsluv->rgb [h s l]))))))
