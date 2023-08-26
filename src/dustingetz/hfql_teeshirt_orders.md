# Teeshirt Orders – HFQL demo

!fiddle-ns[](hfql-demo.hfql-teeshirt-orders/HFQL-teeshirt-orders)


What's happening
* there's a CRUD table, backed by a query
* the table is specified by 4 lines of HFQL + database schema + the clojure.spec for the query
* the filter input labeled `needle` is reflected from the clojure.spec on `orders`, which specifies that the `orders` query accepts a single `string?` parameter named `:needle`
* type "alice" into the input and see the query refresh live

Novel forms
* `hf/hfql`
* `binding`: Electric dynamic scope, it's reactive and used for dependency injection
* `hf/db`
* `hf/*schema*`
* `hf/*nav!*`

Key ideas
* HFQL's mission is to let you model CRUD apps in as few LOC as possible.
* HFQL generalizes graph-pull query notation into a declarative UI specification language.
* spec-driven UI
* macroexpands down to Electric
* network-transparent
* composes with Electric as e/defn
* scope

Dependency injection with Electric bindings