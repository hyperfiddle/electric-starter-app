A largrer example of a HTML table backed by a server-side query. Type into the input and see the query update live.

Novel forms:
* `e/offload`: move a blocking Clojure computation to a threadpool (server only)
* `ui/input`: a latency-stabilized input control with a high level interface
* `e/for-by`: a reactive map operator, stabilized to bind each child branch state (e.g. DOM element) to an entity in the collection by id (provided by userland fn). Similar to [React.js key](https://stackoverflow.com/questions/28329382/understanding-unique-keys-for-array-children-in-react-js/43892905#43892905).

Key ideas:
* `jvm-system-properties` is meant to represent a "query", it's an ordinary Clojure function, written in Clojure
* Note there is no debounce delay. Electric uses the DAG to stream the input to the server, refresh the query.
* We use `e/offload` to move the query to a threadpool, so as to not block the async execution if the query is slow. (Electric functions are fully async, a consequence of being reactive.) 
* When `search` changes, `e/offload` will cancelling outstanding queries whose result is no longer desired by throwing `ThreadInterruptException` in the worker thread.
* We use `e/for-by` (reactive for) to efficiently render the table. `e/for-by` ensures that each table row is bound to a logical element of the collection, and only touched when a row dependency changes.

How to reason about network transfer?
* Look at which remote scope values are closed over and accessed. 
* In the `e/for-by`, `k` and `v` exist in a server scope, and yet are accessed from a client scope.
* Electric tracks this and sends a stream of individual `k` and `v` updates over network.
* The collection value `system-props` is not accessed from client scope, so Electric will not move it. Values are only moved if they are accessed.

For more information about reactive network efficiency, see [this clojureverse answer](https://clojureverse.org/t/electric-clojure-a-signals-dsl-for-fullstack-web-ui/9788/32?u=dustingetz).
