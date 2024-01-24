(ns uix-demo.fiddles
  (:require
   [hyperfiddle.electric :as e]
   [hyperfiddle.electric-dom2 :as dom]
   [uix-demo.demo :refer [UixInterop]]))

;; Dev entrypoint
;; Entries will be listed on the dev index page (http://localhost:8080)
(e/def fiddles {`UixInterop UixInterop})

;; Prod entrypoint, called by `prod.clj`
(e/defn FiddleMain [ring-request]
  (e/server
    (binding [e/http-request ring-request] ; make ring request available through the app
      (e/client
        (binding [dom/node js/document.body] ; where to mount dom elements
          (UixInterop.))))))
