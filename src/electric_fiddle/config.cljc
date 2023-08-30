(ns electric-fiddle.config
  #?(:cljs (:require-macros [electric-fiddle.config :refer [install-fiddle]]))
  (:require [clojure.edn :as edn]
            #?(:clj [clojure.java.io :as io])
            [contrib.template :refer [load-resource]]
            [hyperfiddle.electric :as e]))

; prod only, baked into client and server. nil during build.cljc
(def electric-prod-manifest (load-resource "public/electricfiddle-manifest.edn"))

#?(:clj (def datomic-conn))
#?(:clj (def config 
          (merge
            {:host "0.0.0.0", :port 8080, 
             :resources-path "public" 
             :manifest-path "public/js/manifest.edn"} ; shadow build manifest
            electric-prod-manifest)))

#?(:clj (def ^:dynamic *hyperfiddle-user-ns* nil)) ; cljs comptime, see build.clj
(defmacro install-fiddles []
  (when *hyperfiddle-user-ns* ; nil in dev
    (symbol (name *hyperfiddle-user-ns*) "fiddles")))

; binding fiddles in entrypoint fixes comptime stackoverflow
(e/def pages #_(install-fiddles)) ; client