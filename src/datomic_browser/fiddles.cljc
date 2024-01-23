(ns datomic-browser.fiddles
  (:require [contrib.assert :refer [check]]
            [contrib.clojurex :refer [bindx]]
            #?(:clj [contrib.datomic-contrib :as dx])
            electric-fiddle.main
            [hyperfiddle :as hf]
            [hyperfiddle.electric :as e]
            [hyperfiddle.router :as r]
            [datomic-browser.domain :refer [conn db schema]] ; :(
            [datomic-browser.datomic-browser]
            #_models.mbrainz
            #?(:clj [models.teeshirt-orders-datomic :as model])))

(e/defn DatomicBrowser []
  (e/server
    (let [[conn db] (model/init-datomic)]
      (bindx [conn (check conn)
              db (check db)
              schema (check (new (dx/schema> db)))]
                                        ; index-route (or (seq args) [::summary])
        (e/client
          (datomic-browser.datomic-browser/DatomicBrowser.))))))

(e/defn FiddleMain [ring-req]
  (e/client
    (r/router (r/HTML5-History.)
      (e/server
        (DatomicBrowser.)))))

(e/def fiddles
  {`DatomicBrowser DatomicBrowser})
