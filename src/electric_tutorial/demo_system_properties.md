# System Properties

A larger example of a HTML table backed by a server-side query. Type into the input and see the query update live.

!fiddle-ns[](electric-tutorial.demo-system-properties/SystemProperties)

What's happening

* There's a HTML table on the frontend, backed by a backend "query" `jvm-system-properties` 
* The backend query is an ordinary Clojure function that only exists on the server.
* Typing into the frontend input causes the backend query to rerun and update the table.
* There's a reactive for loop to render the table.
* The view code deeply nests client and server calls, arbitrarily, even through loops.

Novel forms

* `ui/input`: a text input control with "batteries included" loading/syncing state.
* `e/for-by`: a reactive map operator, stabilized to bind each child branch state (e.g. DOM element) to an entity in the collection by id (provided by userland fn - similar to [React.js key](https://stackoverflow.com/questions/28329382/understanding-unique-keys-for-array-children-in-react-js/43892905#43892905)).

Key ideas

* **ordinary Clojure/Script functions**: `clojure.core/defn` works as it does in Clojure/Script, it's still a normal blocking function and is opaque to Electric. Electric does not mess with the `clojure.core/defn` macro.
* **query can be any function**: return collections, SQL resultsets, whatever
* **direct query/view composition**: `jvm-system-properties`, a server function, composes directly with the frontend DOM table. Thus unifying your code into one paradigm, promoting readability, and making it easier to craft complex interactions between client and server components, maintain and refactor them.
* **reactive-for**: The table rows are renderered by a for loop. Reactive loops are efficient and recompute branches only precisely when needed.
* **network transfer can be reasoned about clearly**: values are only transferred between sites when and if they are used. The `system-props` collection is never actually accessed from a client region and therefore never escapes the server.

Reactive for details

* `e/for-by` ensures that each table row is bound to a logical element of the collection, and only touched when a row dependency changes.
* Notice there is a `println` inside the for loop. This is so you can see the efficient rendering in the browser console. 
* Open the browser console now and confirm for yourself:
  * On initial render, each row is rendered once
  * Slowly input "java.class.path"
  * As you narrow the filter, no rows are recomputed. (The existing dom is reused, so there is nothing to recompute because neither `k` nor `v` have changed for that row.)
  * Slowly backspace, one char at a time
  * As you widen the filter, rows are computed as they come back. That's because they were unmounted and discarded!
  * Quiz: Try setting an inline style "background-color: red" on element "java.class.path". When is the style retained? When is the style lost? Why?

Reasoning about network transfer

* Look at which remote scope values are closed over and accessed.
* Only remote access is transferred. Mere *availability* in scope does not transfer.
* In the `e/for-by`, `k` and `v` exist in a server scope, and yet are accessed from a client scope.
* Electric tracks this and sends a stream of individual `k` and `v` updates over network.
* The collection value `system-props` is not accessed from client scope, so Electric will not move it. Values are only moved if they are accessed.

Network transparent composition is not the heavy, leaky abstraction you might think it is

* The DAG representation of the program makes this simple to do
* The electric core implementation is about 3000 LOC
* Function composition laws are followed, Electric functions are truly functions.
  * Functions are an abstract mathematical object
  * Javascript already generalizes from function -> async function (`async/await`) -> generator function (`fn*/yield`)
  * Electric generalizes further: stream function -> reactive function -> distributed function
* With Electric, you can refactor across the frontend/backend boundary, all in one place, without caring about any plumbing. 
  * Refactoring is an algebraic activity with local reasoning, just as it should be. 
  * Functional programming without the BS
