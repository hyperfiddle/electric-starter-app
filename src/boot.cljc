(ns boot
  (:require
   app.todo-list
   [hyperfiddle.electric :as e]))

#?(:clj (defn with-ring-request [_ring-req] (e/boot-server {} app.todo-list/Todo-list)))
#?(:cljs (def client (e/boot-client {} app.todo-list/Todo-list)))
