What's happening

* The string "blink!" is being mounted/unmounted every 2 seconds
* The mount/unmount "component lifecycle" is logged to the browser console with `println`
* `(BlinkerComponent.)` is being constructed and destructed 
* The timer `e/system-time-secs` is a float and updates at the browser animation rate, let's say 60hz. It's being truncated with `int`.

Novel forms

* `e/system-time-secs` reactive system clock with millisecond precision (expressed as a float) and scheduled through requestAnimationFrame.
* `new`: boots (calls) an `e/fn` or `e/defn`. Clojure's syntax sugar also works, i.e. `(BlinkerComponent.)`.
* `e/on-unmount` : takes a regular (non-reactive) function to run before unmount.
* Why no `e/mount`? The `println` here runs on mount, we'd like to see a concrete use case not covered by this.

Key ideas

* **Electric functions have object lifecycle**: Reactive expressions have a "mount" and "unmount" lifecycle. `println` here runs on "mount" and never again since it has only constant arguments, unless the component is destroyed and recreated.
* **Electric functions are constructed like objects with `new`**: Reagent has ctor syntax too; in Reagent we call components with square brackets. This syntax distinguishes between calling Electric fns vs ordinary Clojure fns. To help remember, we capitalize Electric functions like Reagent/React components.
* **Electric functions are absolutely functions, despite also being objects**: They truly follow function laws, while simultaneously having object aspects. This is a real thing in the dataflow literature. From here on we will refer to Electric functions as "functions" or "objects" as appropriate, depending on which aspects are under discussion.
* **work-skipping**: the reactive timer does not needlessly spam downstream rendering. Once the timer is truncated to `0` or `1`, downstream is only recomputed on the transition.
* **backpressure**: Electric Clojure programs are automatically backpressured by Missionary continuous flows.
* **the reactive clock is lazy**: it only schedules the next tick when the current tick is consumed, which means if you switch to another tab, the browser will stop scheduling animation frames and the clock will pause.

Dynamic extent

- Electric objects have dynamic extent. 
- "Dynamic extent refers to things that exist for a fixed period of time and are explicitly “destroyed” at the end of that period, usually when control returns to the code that created the thing." — from [On the Perils of Dynamic Scope (Sierra 2013)](https://stuartsierra.com/2013/03/29/perils-of-dynamic-scope)
- Electric `if` and other control flow nodes will mount and unmount their child branches (like switching the railroad track). Like [RAII](https://en.wikipedia.org/wiki/Resource_acquisition_is_initialization), this lifecycle is deterministic and intended for performing resource management effects.

New

* Electric `new` is backwards compatible with Clojure/Script's new; if you pass it a class it will do the right thing.
* Q: Why do we need syntax to call Electric fns, why not just use metadata on the var? A: Because lambdas. Electric expressions can call both Electric lambdas and ordinary Clojure lambdas (like the sharp-lambda passed to e-unmount). Due to Clojure being dynamically typed, there's no static information available for the compiler to infer the right call convention in this case.

Work-skipping

* We use `int` to truncate the precision (to either `0` or `1`). The truncation is performed at 60hz, as we want to switch *precisely* on the rising edge of the transition.
* Since `int` is recomputing at 60hz, that means `(= 0 _)` will be checked 60 times per second
* However, in `(= 0 0)` and `(= 0 1)`, the `=` is only computed if the arguments have changed since last time. So, at this point the memoization kicks in and we will skip recomputing the `=` and anything downstream. This is called "work-skipping"

Q: That's inefficient right? Why not use `(js/setTimeout 1000)` to slow down the clock? 
* A: Actually, the fast clock is perfectly efficient due to backpressure.
* If the rendering process can't keep up with the requestAnimationFrame tick rate, it will simply skip frames, like a video game.
* Modern hardware can do 3D transforms in realtime, running this little clock at the browser animation rate is negligible.
* If your computer is powered on and plugged in, you generally want to animate at the highest framerate the device is capable of, up to the max animation rate. This is Electric's default behavior out of the box.

Backpressure
* [Missionary](https://github.com/leonoel/missionary) continuous flows are lazy, they do not do work until sampled.
* Electric is a Clojure to Missionary compiler; it compiles lifts each Clojure form into a Missionary continuous flow, thereby automatically backpressuring every point in the DAG.
* See: [Signals vs Streams, in terms of backpressure](https://www.dustingetz.com/#/page/signals%20vs%20streams%2C%20in%20terms%20of%20backpressure%20%282023%29)
* If you want to customize the backpressure locally at any point, you can drop down into missionary's rich suite of concurrency combinators. 
* `e/system-time-secs` is implemented as a missionary flow.