This works because both sessions share a single JVM which means they subscribe to the same atom.

What's happening

Key ideas
* Pending

Novel forms
* `Pending`:
* `dom/input`: a low level DOM control, used here to implement submit-on-enter
* `dom/node`: the live dom element here, maintained in dynamic scope for point writes.

Discuss why Pending is an exception and how we use that to flash the yellow background as a simple loading indicator.