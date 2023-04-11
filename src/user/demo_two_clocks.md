What's happening

* There are two reactive clocks, one on the frontend and one on the server
* When a clock updates, the view incrementally recomputes to stay consistent
* The server clock streams to the client over websocket
* The expression is full-stack – it has frontend parts and backend parts
* The Electric compiler infers the backend/frontend boundary and generates the full-stack app (client and server that coordinate)
* Network sync is automatic and invisible

Novel forms

* `e/defn`: defines an Electric function, which is reactive. Electric fns follow all the same rules as ordinary Clojure functions.
* `e/client`, `e/server`: compile time markers, valid in any Electric body
* `e/system-time-ms`: reactive clock, defined with [Missionary](https://github.com/leonoel/missionary)
* `hyperfiddle.electric-dom`: reactive DOM rendering combinators

Key ideas

* **multi-tier**: the `TwoClocks` function spans both frontend and backend, which are developed together in a single programming language and compilation unit. See [Multitier programming (wikipedia)](https://en.wikipedia.org/wiki/Multitier_programming)
* **reactive**: the function body is reactive, keeping the DOM in sync with the clocks. Both the frontend and backend parts of the function are reactive.
* **network-transparent**: the network is also reactive. the function transmits data over the network (as implied by the AST) in a way which is invisible to the application programmer. See: [Network transparency (wikipedia)](https://en.wikipedia.org/wiki/Network_transparency)
* **streaming lexical scope**: this is not RPC (request/response), that would be too slow. The server streams `s` without being asked, because it knows the client depends on it. If the client had to request each server clock tick, the timer would pause visibly between each request.
* **dom rendering is free**: Electric is already a general-purpose reactive engine, so electric-dom is nearly trivial, it's just 300 LOC of mostly syntax helpers only. There is neither virtual dom, reconcilier, nor diffing.

Electric is a reactivity compiler

* Electric has a DAG-based reactive evaluation model for fine-grained reactivity.
* Electric uses macros to compile actual Clojure syntax into a DAG, using an actual Clojure/Script analyzer.
* Unlike React.js, reactivity is granular to the expression level, not the function level.
  * (This is unlike React.js, where reactivity is granular to the function level.)

Electric code is analyzed at the expression level.

* `e/defn` defines functions that Electric can analyze the body of
* Each expression, e.g. `(- s c)`, is a node in the DAG.  
  * Each expression is async
  * Each expression is reactive
* Arguments, e.g. `s`, is an edge in the DAG.
* Expressions are recomputed when any argument updates.

To visualize the DAG:

* node `(- s c)` has:
  * three incoming edges `#{- s c}`, and
  * one outgoing edge—the anonymous result—pointing to `(dom/text "skew: " _)`.
  * So, if `s` changes, `(- s c)` is recomputed using memoized `c`, and then the `dom/text` reruns (a point write).
  
There is an isomorphism between programs and DAGs

* you already knew this, if you think about it – see [call graph (wikipedia)](https://en.wikipedia.org/wiki/Call_graph)
* The DAG is an abstract representation of a program
* The DAG contains everything there is to know about the flow of data through the Electric program
* Electric uses this DAG to drive reactivity, so we sometimes call the DAG a "reactivity graph". 
* But in theory, this DAG is abstract and there could be evaluated (interpreted or compiled) in many ways.
* E.g., in addition to driving reactivity, Electric uses the DAG to drive network topology, which is just a graph coloring problem.

Network is reactive at the granularity of individual scope values
* when server clock `s` updates, the new value is streamed over network, bound to `s` on the client, ...
* Everything is already async, so adding a 10ms websocket delay does not add impedance, complexity or code weight!

For a 10min video explainer, see [UIs are streaming DAGs](https://hyperfiddle.notion.site/UIs-are-streaming-DAGs-e181461681a8452bb9c7a9f10f507991).