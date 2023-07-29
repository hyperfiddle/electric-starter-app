# Electric Fiddle

Live app: <https://dustingetz.electricfiddle.net/>

```
$ git submodule update --init --recursive
$ yarn
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
fly launch # generate fly.toml
fly status
fly regions list
fly platform vm-sizes
fly scale show
fly scale vm shared-cpu-4x
fly deploy
https://fly.io/docs/about/pricing/
https://fly.io/docs/apps/scale-machine/
https://community.fly.io/t/how-to-specify-regions-to-run-in/3048

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

fly scale show
fly scale count 1 --region ewr
fly scale count 1 --region cdg
```
