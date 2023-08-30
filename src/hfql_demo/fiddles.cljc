(ns hfql-demo.fiddles
  (:require [hyperfiddle.electric :as e]
            
            [hfql-demo.hfql-teeshirt-orders :refer [HFQL-teeshirt-orders]]))

(e/def fiddles
  {`HFQL-teeshirt-orders HFQL-teeshirt-orders})