# Chat

A multiplayer chat app in 30 LOC, all one file. Try two tabs.

!fiddle-ns[](electric-tutorial.demo-chat/Chat)

What's happening

* Chat messages are stored in an atom on the server
* It's multiplayer, each connected session sees the same messages
* Messages submit on enter keypress (and clear the input)
* All connected clients see new messages immediately
* The background flashes yellow when something is loading

Novel forms

* `Pending`: an exception thrown when the client accesses a remote value that is not yet available
* `try/catch`: reactive try catch
* `dom/input`: a low level DOM control, used here to implement submit-on-enter
* `dom/node`: the live dom node, maintained in dynamic scope for local point writes

Key Ideas

* **multiplayer**: the server global state atom is shared by all clients connected to that server, because they share the same JVM.
* **latency**: `e/client` and `e/server` return reactive values across sites. When a remote value is accessed but not yet available, Electric throws a `Pending` exception.
* **reactive try/catch**: in reactive programming, exceptions (like `Pending`) are ephemeral: they can "go away" when an upstream dependency changes, causing the exception to no longer be thrown.
* **JavaScript interop**: cljs interop forms work as expected, side effects are no problem
* **30 LOC**: where is all the client/server framework boilerplate? No GraphQL, no fetch, no API modeling, no async types, etc. One thing missing in this tutorial is error handling (resilient state sync, i.e. optimistic updates with rollback). Electric bundles UI controls for this out of the box, to be discussed in a future tutorial.

Pending details

* Q: Why is Pending modeled as an exception? It's not exceptional? A: The semantics match
* Any uncaught Pending exceptions are silenced and disarded by the Electric entrypoint
* When Pending is thrown, the `catch` body is mounted (turning the background yellow)
* Eventually the remote value becomes available and the Pending exception "goes away", unmounting the `catch` body and removing the yellow style.

Why does each connected client receive realtime updates?

* each client get's its own "server instance", bound to the websocket session
* `(e/def msgs ...)` is global, therefore shared across all sessions (same JVM)
* other than shared global state, the server instances are independent. All sesison state is isolated and bound to the websocket session, just as HTTP request handlers are bound to a request.
