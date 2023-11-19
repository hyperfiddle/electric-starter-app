(ns electric-starter-app.fiddles
  (:require [hyperfiddle.electric :as e]
            [electric-starter-app.todos :refer [Todo-list]]))

(e/def fiddles
  {`Todo-list Todo-list})