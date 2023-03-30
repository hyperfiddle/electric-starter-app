This demo toggles between client and server with a button. Clicking the button toggles a boolean stored in a server-side atom (reference), and that boolean is used to switch between a client expr and a server expr.

Novel forms:
* `e/def`: defines a reactive value `x`, the body is Electric code
* `e/watch`: derives a reactive flow from a Clojure atom by watching for changes
* `ui/button`: a latency-stabilized button, disabled when callback is pending
* `e/fn`: reactive lambda, supports client/server transfer

Key ideas:
* Traditional "single state atom" UI pattern, except the atom is on the server
* The atom definition is ordinary Clojure code, which works because this is an ordinary .cljc file.
* Only values can be serialized and moved across the network; reference types (like the atom) are unserializable and therefore cannot be moved.
