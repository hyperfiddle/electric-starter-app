(ns datomic-browser.fiddles
  (:require [contrib.assert :refer [check]]
            [contrib.clojurex :refer [bindx]]
            #?(:clj [contrib.datomic-contrib :as dx])
            electric-fiddle.main
            [hyperfiddle :as hf]
            [hyperfiddle.electric :as e]
            [datomic-browser.domain :refer [conn db schema]] ; :(
            [datomic-browser.datomic-browser :refer [DatomicBrowser]]
            #_models.mbrainz
            #?(:clj [models.teeshirt-orders-datomic :as model])))

(e/def fiddles
  {`DatomicBrowser DatomicBrowser})

(e/defn FiddleMain []
  (e/server
    (bindx [conn (check (model/init-datomic))
            db (check model/*$*)
            schema (check (new (dx/schema> db)))]
      ; index-route (or (seq args) [::summary])
      (e/client
        (binding [hf/pages fiddles]
          (electric-fiddle.main/Main.))))))
