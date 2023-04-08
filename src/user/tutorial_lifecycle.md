What's happening

* The string "blink!" is being mounted/unmounted every 2 seconds
* The mount/unmount "component lifecycle" is logged to the browser console with `println`

Key ideas

* **object lifecycle**: Reactive expressions have a "mount" and "unmount" lifecycle.
* **inline effects**:
* **extent**
* **Electric functions are like objects**: Electric functions are capitalized like Reagent/React components, and called with `new`.

Novel forms

* `e/on-unmount` : takes a regular (non-reactive) function to run before unmount.
* `new`: boots (calls) an `e/fn` or `e/defn`.

Mount/unmount

* Why no e/mount?
* `if` and other conditionals mounts the branch matching the condition, and unmount others.

New
* hiccup, dynamic types
* Why is `new` needed? Can't detect Electric fn vs regular fn without a static type system.
* Parallel with React Components / Objects lifecycle.
* Electric lambdas are "DAG values," or "pieces of DAG."
* Electric functions are called with `new` as in `(new BlinkerComponent)` – equivalent to `(BlinkerComponent.)`.
* `(ReactiveFn. arg1 arg2)`