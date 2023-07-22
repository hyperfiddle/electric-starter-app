# Electric Y Combinator — Electric Clojure

by Dustin Getz, 2023 July 24

[Electric Clojure](https://github.com/hyperfiddle/electric/) is a new way to write reactive web apps in Clojure/Script which advertises **strong composition across the frontend/backend boundary**, i.e. network transparent composition  (see [wikipedia: Network transparency](https://en.wikipedia.org/wiki/Network_transparency)).

As a proof of strong composition, and a demonstration of how this is different from weaker forms of composition like React Server Components, we offer **distributed Y-combinator** and a demo of using it to recursively walk a server filesystem hierarchy and render a browser HTML frontend, in one pass.

If this is your first time seeing Electric, start with the [live tutorial](https://electric.hyperfiddle.net/).

### Figure 1: Fibonacci with Electric Y Combinator

!fiddle-ns[](dustingetz.y-fib/Y-fib)

What's happening
- Recursive fibonacci
- The recursion is traced to the dom by side effect
- There is **no self-recursion**: we use `Y` to inject recursion into `Fib` via higher order fn `Recur` rather than `Fib` calling itself directly by its name.
- As a reminder, Electric functions are called with `new`
- there is lexical closure, e.g. `Gen` and `F` inside `Y` definition

Key ideas
- The **Y Combinator** (wikipedia: [Fixed-point combinator](https://en.wikipedia.org/wiki/Fixed-point_combinator)) captures the essence of recursive iteration in a simple lambda expression. ChatGPT can tell you more about this.
- Y works in Electric Clojure, demonstrating that Electric lambda *is* lambda.
- Note: you don't actually need `Y` for recursion, this is just a demo.

Ok, lets try something harder:

### Figure 2: Recursive walk over server file system, streamed to DOM

!fiddle-ns[](dustingetz.y-dir/Y-dir)

What's happening
- Recursive file system traversal over "./src" directory on the server
- there's a DOM text input that filters the tree, try typing "electric", both client and server refresh live
- Automatic incremental/streaming network (i.e. **streaming lexical scope**), not request/response. No dataloaders
- **direct frontend/backend composition**: the tree walker, `Dir-tree`, interweaves `e/client` and `e/server` forms arbitrarily. e.g. L26 composes `dom/li` with `file-get-name`, the filename streams across the boundary in mid-flight.
- the network topology is complex, and irrelevant – Electric takes care of it

Key ideas
- **network-transparent composition**: the Electric functions transmit data over the network (as implied by the AST) in a way which is invisible to the application programmer
- **strong composition**: it's actual lambda, which means it scales with complexity. Higher order functions, closures, recursion, are the exact primitive you need to build rigorous abstractions that don't leak.
- **reactive recursion**: the DOM resources are reused across reaction frames, they are not recreated unnecessarily.



# Conclusion

Electric Clojure's goal is to raise the abstraction ceiling in web development, by making it so that your application logic can be expressed out of nothing but lambda. And we really mean it. Remember "Functional Core Imperative Shell"? Where is the imperative shell here?

Lambda is the difference between abstraction and boilerplate. I challenge you to take a hard look at your current web project, stare at whatever convoluted machine you have to deal with this week, and ask yourself: Why does this exist? What artificial problem is preventing this from being a 20 line expression?

P.S. You'll notice in the URL that we've essentially URL-encoded an S-expression and routed to it. Why do you suppose we do that? ... *This essay is a function.*

---

To learn more about Electric, check out the [live tutorial](https://electric.hyperfiddle.net/), the [github repo](https://github.com/hyperfiddle/electric/) or [clone the starter app](https://github.com/hyperfiddle/electric-starter-app) and start playing.