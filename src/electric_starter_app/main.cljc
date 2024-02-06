(ns electric-starter-app.main
  (:require [hyperfiddle.electric :as e]))

(defmacro fn-tower [n & body]
  (if (pos-int? n)
    `(new (e/fn [] (fn-tower ~(dec n) ~@body))) ; fails with e/fn, works with e/fn*
    `(do ~@body)))

(e/defn Main [ring-req]
  (e/client
    (fn-tower 10 (prn "hello"))))

;; Run this in js console:
;; dev.reactor.call(null); dev.start_BANG_();
