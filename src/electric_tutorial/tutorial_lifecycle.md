# Component Lifecycle — Electric tutorial

mount/unmount component lifecycle

!fiddle-ns[](electric-tutorial.tutorial-lifecycle/Lifecycle)

What's happening

* The string "blink!" is being mounted/unmounted every 2 seconds
* The mount/unmount "component lifecycle" is logged to the browser console with `println`
* `(BlinkerComponent.)` is being constructed and destructed 

Novel forms

* `new`: calls an `e/fn` or `e/defn`. Here, `(BlinkerComponent.)` is desugared by Clojure to `(new BlinkerComponent)`, these two forms are identical.
* `e/on-unmount` : takes a regular (non-reactive) function to run before unmount.
* Why no `e/mount`? The `println` here runs on mount without extra syntax needed, we'd like to see a concrete use case not covered by this.

Key ideas

* **Electric functions have object lifecycle**: Reactive expressions have a "mount" and "unmount" lifecycle. `println` here runs on "mount" and never again since it has only constant arguments, unless the component is destroyed and recreated.
* **Call Electric fns with `new`**: Reagent has ctor syntax too; in Reagent we call components with square brackets. This syntax distinguishes between calling Electric fns vs ordinary Clojure fns. To help remember, we capitalize Electric functions, same as Reagent/React components.
* **Electric fns are both functions and objects**: They compose as functions, they have object lifecycle, and they have state. From here on we will refer to Electric fns as both "function" or "object" as appropriate, depending on which aspects are under discussion. We also sometimes refer to "calling" Electric fns as "booting" or "mounting".
* **DAG as a value**: Electric lambdas `e/fn` are values, these can be thought of as "higher order DAGs", "DAG values" or "pieces of DAG".
* **process supervision**

New

* Electric `new` is backwards compatible with Clojure/Script's new; if you pass it a class it will do the right thing.
* Q: Why do we need syntax to call Electric fns, why not just use metadata on the var? A: Because lambdas. 
  * Electric expressions can call both Electric lambdas and ordinary Clojure lambdas (like the sharp-lambda passed to e-unmount). 
  * Due to Clojure being dynamically typed, there's no static information available for the compiler to infer the right call convention in this case. 
  * That's why Reagent uses `[F]` and Electric uses `(F.)`. Note both capitalize `F`!

Dynamic extent

- Electric objects have *dynamic extent*.
- "Dynamic extent refers to things that exist for a fixed period of time and are explicitly “destroyed” at the end of that period, usually when control returns to the code that created the thing." — from [On the Perils of Dynamic Scope (Sierra 2013)](https://stuartsierra.com/2013/03/29/perils-of-dynamic-scope)
- Like [RAII](https://en.wikipedia.org/wiki/Resource_acquisition_is_initialization), this lifecycle is deterministic and intended for performing resource management effects.

Process supervision
- Electric `if` and other control flow nodes will mount and unmount their child branches (like switching the railroad track). 
- If an `e/fn` were to be booted inside of an `if`, the lifetime of the booted lambda is the duration for which the branch of the `if` is active.
- Electric objects can manage references (e.g. DOM node or atom in lexical scope).
- A managed reference's lifetime is tied to the supervising object's lifetime.

Object state

- Recall that Electric functions are auto-memoized. This memo buffer can be seen as the *object state*.
- The memo buffer is discarded and reset when this happens.
- In other words: Electric flows are not [*history sensitive*](https://blog.janestreet.com/breaking-down-frp/). (I hesitate to link to this article from 2014 because it contains confusion/FUD around the importance of continuous time, but the coverage of history sensitivity is good.)