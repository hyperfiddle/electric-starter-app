What's happening
* There are two reactive clocks, one on the frontend and one on the server
* When a clock updates, the view incrementally recomputes to stay consistent
* The server clock streams to the client over websocket
* The expression is full-stack – it has frontend parts and backend parts
* The Electric compiler infers the backend/frontend boundary and generates the full-stack app (client and server that coordinate)
* Network sync is automatic and invisible

Key ideas
* **multitier**: the `TwoClocks` function spans both frontend and backend, which are both developed in a single programming language and compilation unit. See [Multitier programming (wikipedia)](https://en.wikipedia.org/wiki/Multitier_programming)
* **reactive**: the function body is reactive, keeping the DOM in sync with the clocks.
* **network-transparent**: the function transmits data over the network in an implied manner which is invisible to the application programmer. See: [Network transparency (wikipedia)](https://en.wikipedia.org/wiki/Network_transparency)
* **streaming lexical scope**: this is the consequence of the first three properties, and a good mental model for how Electric Clojure works.

Novel forms
* `e/defn`: defines an Electric function, which is reactive. Electric fns follow all the same rules as ordinary Clojure functions.
* `e/client`, `e/server`: compile time markers, valid in any Electric body
* `e/system-time-ms`: reactive clock, defined with [Missionary](https://github.com/leonoel/missionary)
* `hyperfiddle.electric-dom`: reactive DOM rendering combinators. Electric is already a general-purpose reactive engine, so electric-dom is nearly trivial, it's just 300 LOC of mostly syntax helpers only. There is neither virtual dom, reconcilier, nor diffing.

Reactive evaluaton model basics
* Electric has a DAG-based reactive evaluation model for fine-grained reactivity.
* Unlike React.js, reactivity is granular to the expression level, not the function level.
  * Each expression, e.g. `(- s c)`, is a node in the DAG.
  * Lexical scope, e.g. `c`, is an edge in the DAG.
  * Expressions are recomputed when any argument updates. 
  * So, if `c` changes, `(- s c)` is recomputed using memoized `s`.

Electric is a compiler
* the Electric macros analyize the Electric body into a DAG (aka "reactivity graph").
* To see the DAG: `(- s c)` has three incoming edges (`s`, `c`, and `-`) and one outgoing edge to `(dom/text "skew: " _)`.
* The DAG contains everything there is to know about the flow of data through the Electric program.

Network model
* when server clock `s` updates, the new value is streamed over network, bound to `s` on the client, ...
* this is not RPC! The server streams `s` optimistically because it knows the client depends on it, as reified in the DAG.

See [UIs are streaming DAGs](https://hyperfiddle.notion.site/UIs-are-streaming-DAGs-e181461681a8452bb9c7a9f10f507991) for more details.