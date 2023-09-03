(ns electric-fiddle.config
  #?(:cljs (:require-macros [electric-fiddle.config :refer [install-fiddle]]))
  (:require [clojure.edn :as edn]
            #?(:clj [clojure.java.io :as io])
            [contrib.template :refer [comptime-resource]]
            [hyperfiddle.electric :as e]))

#?(:clj (def ^:dynamic *electric-user-version* nil)) ; cljs comptime, see build.clj
#?(:clj (def ^:dynamic *hyperfiddle-user-ns* nil)) ; cljs comptime, see build.clj

#?(:clj (def datomic-conn))

; binding fiddles in entrypoint fixes comptime stackoverflow
(e/def pages #_(install-fiddles)) ; client