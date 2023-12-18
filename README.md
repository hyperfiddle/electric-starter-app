# Electric Fiddle

The fastest way to get started with Electric Clojure.

<!-- Live app: <https://dustingetz.electricfiddle.net/> -->

## Quick Start

```shell
$ git submodule update --init --recursive
$ yarn
```

Begin with an example "Hello World" fiddle:

```shell
$ clj -A:dev
```
```clojure
(dev/-main)
;; => INFO  dev: {:host "0.0.0.0", :port 8080, :resources-path "public", :manifest-path "public/js/manifest.edn"}
;; => INFO  dev: Starting Electric compiler and server...
;; => shadow-cljs - nREPL server started on port 9001
;; => [:dev] Configuring build.
;; => [:dev] Compiling ...
;; => [:dev] Build completed. (231 files, 2 compiled, 0 warnings, 2.46s)
;; => INFO  electric-fiddle.server: 👉 http://0.0.0.0:8080
;; => Loading fiddle: hello-fiddle
;; => Loaded: hello-fiddle.fiddles
```

1. Navigate to [http://localhost:8080](http://localhost:8080)
2. Corresponding source code is in `src-fiddles/hello_world`

## Load more fiddles

Let’s load the Electric Tutorial fiddle. It requires some extra dependencies.
```shell
$ clj -A:dev:electric-tutorial
```
At the REPL:
```clojure
(dev/-main)
;; => ...
;; => INFO  electric-fiddle.server: 👉 http://0.0.0.0:8080

(dev/load-fiddle! 'electric-tutorial)
;; => Loading fiddle: electric-tutorial
;; => Loaded: electric-tutorial.fiddle
```
In your browser, a new entry entry for `electric-fiddle` popped up.

Optional:
```clojure
(dev/unload-fiddle! 'hello-world)
```

## Roll your own

1. `mkdir src-fiddles/my_fiddle`
3. Add the following to `src-fiddles/my_fiddle/fiddles.cljc`:
```clojure
(ns my-fiddles.fiddles
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

(e/defn MyFiddle []
  (e/client
    (dom/h1 (dom/text "Hello from my fiddle."))))

(e/def fiddles ; Entries for the dev index
  {`MyFiddle MyFiddle})

(e/defn FiddleMain [ring-req] ; prod entrypoint
  (e/server
    (binding [e/http-request ring-req])
      (e/client
        (binding [dom/node js/document.body]
          (MyFiddle.)))))
```

At the REPL:
```clojure
(dev/load-fiddle! 'my-fiddle)
```

If your fiddle requires extra dependencies:

- add them as an alias in `deps.edn`:

```clojure
{:aliases {:my-fiddle {:extra-deps {my.extra/dependency {:mvn/version "123"}}}}}
```

- Restart your REPL with the new alias: `$ clj -A:dev:my-fiddle`

# Prod build

Deploys one fiddle at a time.

## "Hello World" prod build

```shell
$ clojure -X:build:prod build-client :hyperfiddle/domain hello-fiddle # :debug false :verbose false :optimize true
$ clj -M:prod -m prod
```

## With extra dependencies
```shell
$ clojure -X:build:prod:electric-tutorial build-client :hyperfiddle/domain electric-tutorial
$ clj -M:prod:electric-tutorial -m prod
# http://localhost:8080/electric-tutorial.tutorial!%54utorial/electric-tutorial.demo-two-clocks!%54wo%43locks
```

# Uberjar

```shell
$ clojure -X:build:prod build-client :hyperfiddle/domain hello-fiddle
$ clojure -X:build uberjar :build/jar-name "app.jar"
```

# Fly.io deployment

```shell
$ fly deploy --remote-only --config src/hello_fiddle/fly.toml
```

<!-- Triage / for reference

```shell
$ clojure -X:build:prod:electric-tutorial build-client :hyperfiddle/domain electric-tutorial :debug true
$ clojure -X:build:prod:hfql_demo build-client :hyperfiddle/domain hfql-demo :debug true
$ clojure -X:build:prod:dustingetz build-client :hyperfiddle/domain dustingetz :debug true
# http://localhost:8080/electric-fiddle.essay!Essay/electric-y-combinator


$ fly deploy --remote-only --config src/hello_fiddle/fly.toml

$ clojure -X:build build-client :verbose true
$ clojure -X:build uberjar :build/jar-name "app.jar" :verbose true
```

* note, build uses -X not -T, build/app classpath contamination cannot reasonably be prevented. see https://www.notion.so/hyperfiddle/logger-epic-303a8024a8fd4b09a40a67871d3161cf?pvs=4

```shell
docker login
docker ps
docker build -t hyperfiddle/photon-demo .
docker run -dP hyperfiddle/photon-demo
docker run -it hyperfiddle/photon-demo bash
docker push hyperfiddle/photon-demo:latest
docker build --platform linux/amd64 -t hyperfiddle/photon-demo .
docker buildx build --platform linux/amd64,linux/arm64 -t hyperfiddle/photon-demo .
# https://hub.docker.com/_/clojure
# Apple Silicon requires --platform linux/amd64

brew install flyctl
fly auth signup
fly auth login
fly launch # project wizard, then go to https://fly.io/dashboard/personal
fly deploy --build-only # test remotely to workaround Apple Silicon docker issues
fly status
fly regions list
flyctl regions add cdg
fly platform vm-sizes
fly scale show
fly scale vm shared-cpu-4x
fly scale count 1 --region ewr
fly scale count 1 --region cdg
fly scale count 4 --max-per-region 1
#https://community.fly.io/t/how-to-specify-regions-to-run-in/3048
fly deploy
https://fly.io/docs/about/pricing/
https://fly.io/docs/apps/scale-machine/
https://community.fly.io/t/how-to-specify-regions-to-run-in/3048
cost = $41/mo for dedicated-cpu-1x, 4GB ram -- https://fly.io/docs/about/pricing/

# DNS
fly ips list
fly ips allocate-v4
# configure DNS A and AAAA records
fly certs create "*.electricfiddle.net" # quote * to avoid shell expansion
fly certs list
fly certs check "*.electricfiddle.net"
fly certs show "*.electricfiddle.net"

https://dustingetz.electricfiddle.net/
https://www.electricfiddle.net/
https://electricfiddle.net/

# Github actions
fly tokens create deploy
# https://github.com/hyperfiddle/electric-fiddle/settings/secrets/actions/

npx jamsocket create photon-demo
npx jamsocket service create photon-demo
npx jamsocket push photon-demo hyperfiddle/photon-demo:latest
npx jamsocket spawn photon-demo
```
-->
