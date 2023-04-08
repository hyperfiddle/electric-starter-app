# electric-starter-app

```
$ clj -A:dev -X user/main

Starting Electric compiler and server...
shadow-cljs - server version: 2.20.1 running at http://localhost:9630
shadow-cljs - nREPL server started on port 9001
[:app] Configuring build.
[:app] Compiling ...
[:app] Build completed. (224 files, 0 compiled, 0 warnings, 1.93s)

👉 App server available at http://0.0.0.0:8080
```

# Deployment

ClojureScript optimized build, Dockerfile, Uberjar, Github actions CD to fly.io

```
clojure -X:build build-client          # optimized release build
clojure -X:build uberjar               # contains demos and demo server, currently
HYPERFIDDLE_ELECTRIC_APP_VERSION=`git describe --tags --long --always --dirty`
docker build --build-arg VERSION='"'$HYPERFIDDLE_ELECTRIC_APP_VERSION'"' -t electric-starter-app .
docker run --rm -p 7070:8080 electric-starter-app
# flyctl launch ... ? create fly app, generate fly.toml, see dashboard
# https://fly.io/apps/electric-starter-app

NO_COLOR=1 flyctl deploy --build-arg VERSION='$HYPERFIDDLE_ELECTRIC_APP_VERSION'
# https://electric-starter-app.fly.dev/
```

- `NO_COLOR=1` disables docker-cli fancy shell GUI, so that we see the full log (not paginated) in case of exception
- `--build-only` tests the build on fly.io without deploying
