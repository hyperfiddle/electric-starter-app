What's happening

* `blink!` is added and removed from the DOM every two seconds
* `component-did-mount` is printed to the console on mount
* `component-will-unmount` is printed to the console on unmount

Key ideas
* Reactive expressions have a "mount" and "unmount" lifecycle.
* Electric functions are called with `new` as in `(new BlinkerComponent)` – equivalent to `(BlinkerComponent.)`.
  * `(ReactiveFn. arg1 arg2)`
* `if` and other conditionals mounts the branch matching the condition, and unmount others.

Novel forms
* `e/on-unmount` : takes a regular (non-reactive) function to run before unmount.
* `new`: calls an `e/fn` or `e/defn`.


Discuss:
* why we need `e/on-unmount` but not `e/on-mount`.
* Why is `new` needed?
  * Can't detect Electric fn vs regular fn without a static type system.
  * Parallel with React Components / Objects lifecycle.
* Why is `BlinkerComponent` camel-cased?
