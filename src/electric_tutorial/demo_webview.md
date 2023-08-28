# Webview — Electric tutorial

A database backed webview with reactive updates.

!fiddle-ns[](electric-tutorial.demo-webview/Webview)

What's happening
* The webview is subscribed to the database, which updates with each transaction.
* If you ran the transact forms at the bottom in your REPL, the view would update reactively.

Novel forms
* `e/watch` on datascript connection
* `e/offload` run a blocking function (i.e. query) on threadpool, JVM only

Key ideas
* Datascript is on the server, it can be any database
* Direct query/view composition, with a loop
* Electric is fully asynchronous, don't block the event loop!