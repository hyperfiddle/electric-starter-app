What's happening
* There is auth
* When logged in, you can see a list of other users present (try two tabs)
* The input flashes yellow now when pending, not the page.

Key ideas
* Ordinary Clojure web server, you control it.
* Auth is same as any other web app, you have access to the request.
* Electric lambdas are "DAG values," or "pieces of DAG."
* `dom/on` is managing the lifetime (extent) of the keydown callback by observing `Pending` exceptions

Novel forms
* `e/*http-request*` the ring request that established the websocket connection
* `do` sequences effects, returning the final result (same as Clojure). Reactive objects in the body run concurrently, so e.g. `(do (BlinkerComponent.) (BlinkerComponent.))` will have concurrent blinkers.

HTTP server details
* [`electric_server_java8_jetty9.clj`](https://github.com/hyperfiddle/electric-examples-app/blob/main/src/electric_server_java8_jetty9.clj)
