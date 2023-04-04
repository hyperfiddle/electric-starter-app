(ns user.demo-chat
  (:import [hyperfiddle.electric Pending])
  (:require [contrib.data :refer [pad]]
            [contrib.str :refer [empty->nil]]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

#?(:clj (defonce !msgs (atom (list))))
(e/def msgs (e/server (pad 10 nil (e/watch !msgs))))

(e/defn Chat []
  (e/client
    (try
      (dom/ul
        (e/server
          (e/for-by identity [msg (reverse msgs)] ; chat renders bottom up
            (e/client
              (dom/li (dom/style {:visibility (if (nil? msg)
                                                "hidden" "visible")})
                (dom/text msg))))))

      (dom/input
        (dom/props {:placeholder "Type a message"})
        (dom/on "keydown" (e/fn [e]
                            (when (= "Enter" (.-key e))
                              (when-some [v (empty->nil (.. e -target -value))]
                                (e/server (swap! !msgs #(cons v (take 9 %))))
                                (set! (.-value dom/node) ""))))))
      (catch Pending e
        (dom/style {:background-color "yellow"})))))