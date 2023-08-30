(ns electric-fiddle.config
  #?(:cljs (:require-macros [electric-fiddle.config :refer [install-fiddle]]))
  (:require [hyperfiddle.electric :as e]))

#?(:clj (def app-version (System/getProperty "HYPERFIDDLE_ELECTRIC_SERVER_VERSION")))
#?(:clj (def datomic-conn))
#?(:clj (def electric-server-config 
          {:host "0.0.0.0", :port 8080, 
           :resources-path "public" 
           :manifest-path "public/js/manifest.edn"})) ; shadow output

#?(:clj (def ^:dynamic *hyperfiddle-user-ns* nil)) ; cljs comptime, see build.clj
(defmacro install-fiddles []
  (when *hyperfiddle-user-ns* ; nil in dev
    (symbol (name *hyperfiddle-user-ns*) "fiddles")))

; binding fiddles in entrypoint fixes comptime stackoverflow
(e/def pages #_(install-fiddles)) ; client