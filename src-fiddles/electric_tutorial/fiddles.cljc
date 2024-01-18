(ns electric-tutorial.fiddles
  (:require [hyperfiddle.electric :as e]
            [electric-fiddle.main]
            [hyperfiddle :as hf]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.router :as r]

            [electric-tutorial.tutorial :refer [Tutorial]]))

(e/def fiddles
  {`Tutorial Tutorial})

(e/defn FiddleMain [ring-req]
  (e/server
    (binding [e/http-request ring-req]
      (e/client
        (binding [dom/node js/document.body
                  hf/pages fiddles]
          (r/router (r/HTML5-History.)
            (e/server
              (Tutorial.))))))))
