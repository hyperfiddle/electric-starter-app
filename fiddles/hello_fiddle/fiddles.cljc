(ns hello-fiddle.fiddles
  (:require [hyperfiddle.electric :as e]
            [hello-fiddle.hello-fiddle :refer [Hello]]))

(e/def fiddles ; prod.clj auto-resolves this global via electric-manifest.edn
  {`Hello Hello})