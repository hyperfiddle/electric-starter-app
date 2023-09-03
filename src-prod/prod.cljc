(ns prod
  #?(:cljs (:require-macros [prod :refer [install-user-fiddles]]))
  (:require #?(:clj [clojure.tools.logging :as log])
            [contrib.assert :refer [check]]
            [contrib.template :refer [comptime-resource]]
            electric-fiddle.main
            #?(:clj [electric-fiddle.server :refer [start-server!]])
            [hyperfiddle :as hf]
            [hyperfiddle.electric :as e]
            #?(:cljs #=(clojure.core/identity hyperfiddle/*hyperfiddle-user-ns*)))) ; domain DI here

(def config
  (merge
    (comptime-resource "electric-manifest.edn") ; prod only, baked into both client and server, nil during build
    {:host "0.0.0.0", :port 8080,
     :resources-path "public"
     :manifest-path "public/js/manifest.edn"})) ; shadow build manifest

(defmacro install-user-fiddles [] (symbol (name hf/*hyperfiddle-user-ns*) "fiddles"))

#?(:clj
   (defn -main [& {:strs [] :as args}] ; clojure.main entrypoint, args are strings
     (log/info (pr-str args))
     (alter-var-root #'config #(merge % args))
     (check string? (::e/user-version config))
     (check string? (::hf/domain config))
     (require (symbol (str (::hf/domain config) ".fiddles"))) ; load userland server
     (start-server! config)))

#?(:cljs
   (do
     (def electric-entrypoint
       (e/boot
         (binding [hf/pages (install-user-fiddles)]
           (electric-fiddle.main/Main.))))
     
     (defonce reactor nil)

     (defn ^:dev/after-load ^:export start! []
       (set! reactor (electric-entrypoint
                       #(js/console.log "Reactor success:" %)
                       #(js/console.error "Reactor failure:" %))))

     (defn ^:dev/before-load stop! []
       (when reactor (reactor)) ; teardown
       (set! reactor nil))))