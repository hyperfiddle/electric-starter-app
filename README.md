# Examples app — Electric Clojure


```
$ yarn                          # demos only - react interop, codemirror
$ clj -A:dev -X user/main

Starting Electric compiler and server...
shadow-cljs - server version: 2.20.1 running at http://localhost:9630
shadow-cljs - nREPL server started on port 9001
[:app] Configuring build.
[:app] Compiling ...
[:app] Build completed. (224 files, 0 compiled, 0 warnings, 1.93s)

👉 App server available at http://0.0.0.0:8080
```

Deployment

```
fly status
flyctl regions list
flyctl regions add cdg
fly scale count 4 --max-per-region 1
https://community.fly.io/t/how-to-specify-regions-to-run-in/3048
```