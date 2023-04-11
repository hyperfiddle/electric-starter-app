What's happening

- styles
- it's multiplayer! 0 LOC cost
- state is durable (server side database) 0 LOC cost
- managed busy states

Key ideas
- **entire app as a function**: with everything - frontend, backend, state, network, reactivity, dom rendering, resource lifecycle, load states. Unified into just functions.

App as a function:

```clojure
(e/defn TodoMVC-UI [state]
  (dom/section (dom/props {:class "todoapp"})
    (dom/header (dom/props {:class "header"})
      (CreateTodo.))
    (when (e/server (pos? (todo-count db :all)))
      (TodoList. state))
    (dom/footer (dom/props {:class "footer"})
      (TodoStats. state))))
```

- `TodoMVC-UI` is truly a function, it follows function laws, it has all forms of scope, etc
- How can we test this? If it's really a function, and it really composes, that means we can call it in composed ways, like for loops, 
