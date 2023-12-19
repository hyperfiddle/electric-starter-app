(ns electric-demo.wip.js-interop
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric-ui4 :as ui]
            [hyperfiddle.history :as history]))

(e/defn QRCode []
  (e/client
    (let [value  (or (::value history/route) (str js/window.location.origin))
          !ready (atom false)]
      (dom/script
        (dom/props {:src "https://cdn.rawgit.com/davidshimjs/qrcodejs/gh-pages/qrcode.min.js"})
        (dom/on! "load" (fn [_] (reset! !ready true))))
      (if-not (e/watch !ready)
        (dom/p (dom/text "Loading..."))
        (do
          (ui/input value (e/fn [value] (history/swap-route! assoc ::value value)))
          (dom/div
            (let [qrcode (js/QRCode. dom/node (clj->js {:width        128
                                                        :height       128
                                                        :colorDark    "#000000"
                                                        :colorLight   "#ffffff"
                                                        :correctLevel js/QRCode.CorrectLevel.H}))]
              ((fn [value]
                 (.clear qrcode)
                 (.makeCode qrcode value))
               value))))))))