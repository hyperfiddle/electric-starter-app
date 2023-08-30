(ns hello-fiddle.fiddles
  (:require [hyperfiddle.electric :as e]
            hello-fiddle.hello))

(e/def fiddles
  {`hello-fiddle.hello/Hello hello-fiddle.hello/Hello})