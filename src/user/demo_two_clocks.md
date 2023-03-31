Novel forms:
* `e/defn`: defines an Electric function, which is reactive
* `e/client`, `e/server`: compile time markers, valid in any Electric body
* `e/system-time-ms`: reactive clock, defined with [Missionary](https://github.com/leonoel/missionary)

Key ideas: 
* Electric fns follow all the same rules as ordinary Clojure functions
* the Electric macros compile the Electric body into a DAG (aka "reactivity graph")
  * when `c` changes, update the first `dom/text`, recompute `(- s c)` using memoized `s`, update the second `dom/text`
  * when server clock changes, the latest value is streamed over network, bound to `s` on the client, ...
  * this is not RPC; the server streams `s` optimistically because the client depends on it, as reified in the DAG. See [UIs are streaming DAGs](https://hyperfiddle.notion.site/UIs-are-streaming-DAGs-e181461681a8452bb9c7a9f10f507991)
* We target 100% Clojure/Script compatibility
  * Electric implements a proper Clojure/Script analyzer to support all special forms
  * thread macros work, side effects work, platform interop works, etc
