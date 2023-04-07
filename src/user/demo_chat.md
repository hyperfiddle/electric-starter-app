What's happening

* There's a list of messages on the frontend, backed by a server side atom.
* Typing in the frontend input runs a callback on keypress
  * Pressing enter adds the input's content to the server-side atom, then resets the input.
* Server-side state is global, a new message is streamed to all connected clients.
* Up to 10 messages are retained.
* First render and pressing enter throws a `Pending` exception, which turns the page yellow for a brief instant.

Key Ideas

* **global/local state**: is defined by the language's scope itself. State access scope is modeled by the program.
* **Pending**: Establishing a stream (initial render, running an effect on the remote peer) implies an initial latency.
  * Electric models this intial latency by throwing `Pending`.
* **try/catch**: Reactive try catch implies the `try` branch stays up while throwing, and the `catch` branch runs until `try` stops throwing.


Novel forms
* `Pending`: An exception thrown by Electric to model initial latency, on first render or when running an effect on the remote peer.
* `try/catch`: reactive try catch, runs `catch` branch in parallel with `try`.
* `dom/input`: a low level DOM control, used here to implement submit-on-enter.
* `dom/node`: the live dom element here, maintained in dynamic scope for point writes.

Discuss why Pending is an exception and how we use that to flash the yellow background as a simple loading indicator.
