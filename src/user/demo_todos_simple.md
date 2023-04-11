What's happening
* It's a functional todo list, the first "real app"
* <https://github.com/hyperfiddle/electric-starter-app>

Novel forms
* `ui/checkbox`
* `binding` – reactive dynamic scope; today all Electric defs are dynamic.

Key ideas
* dependency injection
* dynamic scope
* unserializable reference transfer - `d/transact!` returns an unserializable ref which cannot be moved over network, when this happens it is typically unintentional, so instead of crashing we warn and send `nil` instead.
* nested transfers, even inside a loop
* query diffing
