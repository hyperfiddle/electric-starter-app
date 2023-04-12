What's happening
* The timer `e/system-time-secs` is a float and updates at the browser animation rate, let's say 120hz.
* Clocks are printed to both the DOM (at 120hz) and also the browser console (at 1hz)
* The clocks pause when the browser page is not visible (i.e. you switch tabs), confirm it

Novel forms
* None

Key ideas
* **work-skipping**: expressions are only recomputed if an argument actually changes, otherwise the previous value is reused. Here, the truncated clock timer does not needlessly spam the println; downstream nodes are only recomputed on the transition.
* **the reactive clocks are lazy**
* **reactive rendering is driven by requestAnimationFrame**
* **backpressure**: Electric Clojure programs are automatically backpressured.

Work-skipping
* We use `int` to truncate the precision. The truncation is performed at 120hz, as we want to switch *precisely* on the rising edge of the transition.
* Since `int` is recomputing at 120hz, that means anything immediately downstream will be checked 120 times per second
  * so both `(println "s" (int s))` and `(- s c)` are both checked at 120hz
* However, expressions only run if at least one argument has changed
* So, at this point the memoization kicks in and we will skip the work, this is called "work-skipping"
* `(println "s" (int s))` prints at 1hz not 120hz

Q: That's inefficient right? Why not use `(js/setTimeout 1000)` to make a slower clock? 
* A: Actually, the fast clock is perfectly efficient due to Electric's backpressure.
* If the rendering process can't keep up with the requestAnimationFrame tick rate, it will simply skip frames, like a video game.
* Modern hardware can do 3D transforms in realtime, running this little clock at the browser animation rate is negligible.
* If your computer is powered on and plugged in, you generally want to animate at the highest framerate the device is capable of, up to the max animation rate. This is Electric's default behavior out of the box.

Backpressure
* What if you're on an old mobile device that can't keep up with the server clock streaming?
* See: [Signals vs Streams, in terms of backpressure](https://www.dustingetz.com/#/page/signals%20vs%20streams%2C%20in%20terms%20of%20backpressure%20%282023%29)
* Electric is a Clojure to [Missionary](https://github.com/leonoel/missionary) compiler; it compiles lifts each Clojure form into a Missionary continuous flow.
* Missionary continuous flows are lazy, they do not do work until sampled.
* Thereby automatically backpressuring every point in the Electric DAG.
* If you want to customize the backpressure locally at any point, you can drop down into missionary's rich suite of concurrency combinators. 

Clock details
* `e/system-time-secs` is implemented as a missionary flow.
* on the client, the underlying clock is implemented with `requestAnimationFrame` such that it only schedules work once the current tick has been consumed.
* That means, if you switch to another tab, the browser will stop scheduling animation frames and the clock will pause.
* on the server, the clock is implemented similarly, it will tick as fast as possible but only when sampled. If nobody is consuming the clock, the clock will not tick.
