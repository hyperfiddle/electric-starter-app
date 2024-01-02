# Electric Fiddle

The easiest way to get started with Electric Clojure.

This is how we fiddle around with stuff at work. All our demos are here.<br>
This is how we recommend you start, because it’s all self contained.

One day your app will have grown and you’ll be ready to eject it. <br>
You’ll delete what you don’t need (there’s not much anyway) and you’ll be good to go.

You can fork this repo or clone it and have your own branch.
We’ll continue to update the main branch.<br>
You’ll be able to track changes and merge them as needed.

## Quick Start

```shell
$ git submodule update --init --recursive
$ yarn
```

Begin with an example "Hello World" fiddle:

```shell
$ clj -A:dev
```
At the REPL:
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
(ns my-fiddle.fiddles
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
$ clojure -X:build:prod uberjar :hyperfiddle/domain hello-fiddle :build/jar-name "app.jar"
$ java -cp app.jar clojure.main -m prod
```

# Fly.io deployment

```shell
$ fly deploy --remote-only --config src/hello_fiddle/fly.toml
```

