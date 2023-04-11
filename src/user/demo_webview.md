What's happening
* The webview is subscribed to the database, which updates with each transaction.
* If you ran the transact forms at the bottom in your REPL, the view would update reactively.

Key ideas
* Datascript is on the server, it can be any database
* Direct query/view composition, with a loop

Novel forms
* `e/watch` on datascript connection