What's happening
* There's a button on the frontend, with a callback, that toggles a boolean, stored in a server-side atom.
* That boolean is used to switch between a client expr and a server expr.
* Both exprs print the platform number type, which is either a java.lang.Long or a javascript Number.
* The resulting string is streamed from server to client over network, and written through to the DOM.

Key Ideas
* **Clojure/Script interop**: The atom definition is ordinary Clojure code, which works because this is an ordinary .cljc file.
* Traditional **"single state atom"** UI pattern, except the atom is on the server.
* **reactive control flow**: `if`, `case` and other Clojure control flow forms are reactive. Here, when `x` toggles, `(case x)` will *switch* between branches. In the DAG, if-nodes look like a [railroad switch (image)](https://clojureverse.org/uploads/default/original/2X/7/7b52e4535db802fb51a368bae4461829e7c0bfe5.jpeg).
* **distributed lambda**: the button callback spans both client and server, just like function `Toggle`.

Novel forms
* `e/def`: defines a reactive value `x`, the body is Electric code
* `e/watch`: derives a reactive flow from a Clojure atom by watching for changes
* `hyperfiddle.electric-ui4`: high level form controls (inputs, typeaheads, etc) with managed loading/syncing states
* `ui/button`: a button with managed load state. On click, it becomes disabled until the callback result—possibly remote—is available.
* `e/fn`: reactive lambda, supports client/server transfer

Client/server value transfer
* Only values can be serialized and moved across the network. 
* Reference types (e.g. atoms, database connections, Java classes) are unserializable and therefore cannot be moved.
* Quiz: in `(e/server (pr-str (type 1)))`, why do we `pr-str` on the server? Hint: what type is `java.lang.Long`?
* Quiz: in `(e/def x (e/server (e/watch !x)))`, why do we `e/watch` on the server?

We target 100% Clojure/Script compatibility
* That means, any valid Clojure expression, when pasted into an Electric body, will evaluate to the same result, and produce the same side effects, in the same order. Electric only extends Clojure, it takes nothing away.
* To achieve this, Electric implements a proper Clojure/Script analyzer to support all Clojure special forms.
* macros work, side effects work, platform interop works, data structures are the same, clojure.core works
* **It's just Clojure**