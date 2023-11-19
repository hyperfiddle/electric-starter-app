# Electric Fiddle

Live app: <https://dustingetz.electricfiddle.net/>

```
$ git submodule update --init --recursive
$ yarn
$ clj -X:dev user/main

Starting Electric compiler and server...
shadow-cljs - server version: 2.20.1 running at http://localhost:9630
shadow-cljs - nREPL server started on port 9001
[:app] Configuring build.
[:app] Compiling ...
[:app] Build completed. (224 files, 0 compiled, 0 warnings, 1.93s)

👉 App server available at http://0.0.0.0:8080
```

# Deployment

```
clojure -X:build:prod:hello-fiddle build-client :hyperfiddle/domain hello-fiddle :debug true
clojure -X:build:prod:electric_tutorial build-client :hyperfiddle/domain electric-tutorial :debug true
# http://localhost:8080/electric-tutorial.tutorial!%54utorial/electric-tutorial.demo-two-clocks!%54wo%43locks
clojure -X:build:prod:hfql_demo build-client :hyperfiddle/domain hfql-demo :debug true
clojure -X:build:prod:electric_demo build-client :hyperfiddle/domain electric-demo :debug true
clojure -X:build:prod:dustingetz build-client :hyperfiddle/domain dustingetz :debug true
# http://localhost:8080/electric-fiddle.essay!Essay/electric-y-combinator

clj -M:prod -m prod
clj -M:prod:datomic-browser -m prod

fly deploy --remote-only --config src/hello_fiddle/fly.toml

clojure -X:build build-client :verbose true
clojure -X:build uberjar :jar-name "app.jar" :verbose true
```

* note, build uses -X not -T, build/app classpath contamination cannot reasonably be prevented. see https://www.notion.so/hyperfiddle/logger-epic-303a8024a8fd4b09a40a67871d3161cf?pvs=4

```
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
