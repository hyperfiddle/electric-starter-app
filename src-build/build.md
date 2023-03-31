# Continuous deployment of demos

```shell
clojure -T:build build-client          # optimized release build
HYPERFIDDLE_ELECTRIC_APP_VERSION=`git describe --tags --long --always --dirty`
docker build --progress=plain --build-arg VERSION=$HYPERFIDDLE_ELECTRIC_APP_VERSION -t electric-examples-app:latest .
docker run --rm -p 7070:8080 electric-examples-app:latest
# flyctl launch ... ? create fly app, generate fly.toml, see dashboard
# https://fly.io/apps/electric-starter-app

NO_COLOR=1 flyctl deploy --build-arg VERSION=$HYPERFIDDLE_ELECTRIC_APP_VERSION
# https://electric-starter-app.fly.dev/
```

- `NO_COLOR=1` disables docker-cli fancy shell GUI, so that we see the full log (not paginated) in case of exception
- `--build-only` tests the build on fly.io without deploying
