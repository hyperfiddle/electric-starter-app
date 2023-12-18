(ns hello-fiddle.fiddles
  (:require [hello-fiddle.hello-fiddle :refer [Hello]]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

(e/def fiddles ; prod.clj auto-resolves this global via electric-manifest.edn
  {`Hello Hello})


(e/defn FiddleMain [ring-req]
  (e/client
    (binding [dom/node js/document.body]
      (Hello.))))
