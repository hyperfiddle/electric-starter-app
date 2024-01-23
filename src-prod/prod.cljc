(ns prod
  #?(:cljs (:require-macros [prod :refer [install-user-inject]]))
  (:require #?(:clj [clojure.tools.logging :as log])
            [contrib.assert :refer [check]]
            [contrib.template :refer [comptime-resource]]
            electric-fiddle.main
            #?(:clj [electric-fiddle.server-jetty :refer [start-server!]])
            [hyperfiddle :as hf]
            [hyperfiddle.electric :as e]
            #?(:cljs #=(clojure.core/identity hyperfiddle/*hyperfiddle-user-ns*)))) ; domain DI here

(def config
  (merge
    (comptime-resource "electric-manifest.edn") ; prod only, baked into both client and server, nil during build
    {:host "0.0.0.0", :port 8080,
     :resources-path "public"
     :manifest-path "public/js/manifest.edn"})) ; shadow build manifest

#?(:clj
   (defn -main [& {:strs [] :as args}] ; clojure.main entrypoint, args are strings
     (alter-var-root #'config #(merge % args))
     (log/info (pr-str config))
     (check string? (::e/user-version config))
     (check string? (::hf/domain config))
     (let [entrypoint (symbol (str (::hf/domain config) ".fiddles") "FiddleMain")]
       (requiring-resolve entrypoint) ; load userland server
       (start-server! (eval `(fn [ring-req#] (e/boot-server {} ~entrypoint ring-req#)))
         config))))

(defmacro install-user-inject [] (symbol (name hf/*hyperfiddle-user-ns*) "FiddleMain"))

#?(:cljs
   (do
     (def electric-entrypoint
       (e/boot-client {}
         ; in prod, fiddle owns the app and there's only one of them
         (let [FiddleMain (install-user-inject)]
           (FiddleMain.))))

     (defonce reactor nil)

     (defn ^:dev/after-load ^:export start! []
       (set! reactor (electric-entrypoint
                       #(js/console.log "Reactor success:" %)
                       #(js/console.error "Reactor failure:" %))))

     (defn ^:dev/before-load stop! []
       (when reactor (reactor)) ; teardown
       (set! reactor nil))))