What's happening
* There is auth
* Presence – logged in users can see who else is in the room (try two tabs)

Novel forms
* `e/*http-request*` the ring request that established the websocket connection
* `do` sequences effects, returning the final result (same as Clojure). Reactive objects in the body are constructed in order and then run concurrently, so e.g. `(do (Blinker.) (Blinker.))` will have concurrent blinkers.

Key ideas
* **regular web server**: Electric's websocket can be hosted by any ordinary Clojure web server, you provide this.
* **auth** is same as any other web app, you have access to the request, cookies, etc

HTTP server details
* App server for this app: [`electric_server_java8_jetty9.clj`](https://github.com/hyperfiddle/electric-examples-app/blob/main/src/electric_server_java8_jetty9.clj)
  * it hosts a websocket
  * it has a `/auth` endpoint configured with HTTP Basic Auth
* Note the server is application code, it's not provided by the Electric library dependency.
* Q: Why is it not included in the library? A: because it has hardcoded http routes and auth examples
* Instead we provide [templates](https://github.com/hyperfiddle/electric-starter-app/tree/main/src) in the starter app for you to modify.

More on Electric functions/objects and dynamic extent
* "Extent" is defined in the [previous tutorial](/user.tutorial-lifecycle!Lifecycle)
* Here, `dom/on`'s callback, the `e/fn`, is an *object* and has *extent*
* Using the railroad switch metaphor, the extent of the object/node is the time during which the callback is active/alive/running.
* `dom/on` is managing the lifetime/extent of the callback `e/fn` by observing `Pending` exceptions it throws.
  * `e/server` throws `Pending` until the result of the body is known to the client (did it succeed? did it throw?)
  * When the `Pending` exception goes away, `dom/on` will unmount the `e/fn` and remove it from the DAG.
  * This means you can put resources (e.g. allocate DOM) inside the callback, as we do here with `(dom/style {:background-color "yellow"})`.

When you send a message and the server effect is pending, now the input flashes yellow instead of the whole page
* The style is mounted when it's parent e/fn is mounted, and the style is unmounted when the e/fn is unmounted.
* So the style's extent is the duration of the callback, and the callback is the duration of the Pending exception plus one tick for the final result. Once the result is known, the callback (and the style) are unmounted and removed from the DAG.