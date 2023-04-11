What's happening
* Thousands of elements efficiently streamed from server to client
* virtual pagination (the same 20 divs are reused — check the DOM and confirm)
* `e/for` is transparently coordinating the network (same as previous [System Properties demo](/user.demo-system-properties!SystemProperties))

Key ideas

* **performance**: `e/for` is awesome, look how fast it is
* **diffing**: `e/for` performs diffing to reuse child state (DOM elements) and streams only deltas (mount/unmount/update/move).
* **network-transparent abstraction**: This demo is [50 LOC](https://github.com/hyperfiddle/electric-examples-app/blob/main/src/user/demo_explorer.cljc) (it also uses a gridsheet component which is [92 LOC](https://github.com/hyperfiddle/electric/blob/master/src/contrib/gridsheet.cljc), so 142 LOC total)
* **compiler managed network**: How would you do this in React?


Network-transparent abstraction is the whole point of Electric Clojure, see <https://github.com/hyperfiddle/electric-datomic-browser> (a more filled out version of basically this same demo) for more content about network-transparent abstraction.

For more detail about reactive network efficiency, see [this clojureverse answer](https://clojureverse.org/t/electric-clojure-a-signals-dsl-for-fullstack-web-ui/9788/32?u=dustingetz).
