(ns hyperfiddle
  (:require [clojure.spec.alpha :as s]
            [hyperfiddle.electric :as e]))

; See discussion of single-segment namespaces here
; https://github.com/bbatsov/clojure-style-guide/pull/100
; Clojure is fine if no gen-class
; ClojureScript compiler warns
; Just use hyperfiddle.api :as hf, for specs as well as fns,
; it's a little ugly but with the require alias it works out.

; public api here, one API ns
; public specs here
; internal specs anywhere

#?(:clj (def ^:dynamic *hyperfiddle-user-ns* nil)) ; cljs comptime, prod only, injected by build-client

; binding fiddles in entrypoint fixes comptime stackoverflow
(e/def pages #_(install-fiddles)) ; client
