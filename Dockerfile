FROM node:14.7-stretch AS node-deps
WORKDIR /app
COPY package.json package.json
RUN npm install

FROM clojure:openjdk-11-tools-deps AS build
WORKDIR /app
COPY --from=node-deps /app/node_modules /app/node_modules
#COPY .m2 /root/.m2
COPY shadow-cljs.edn shadow-cljs.edn
COPY deps.edn deps.edn
COPY src src
COPY src-build src-build
COPY src-prod src-prod
COPY vendor vendor
COPY resources resources
ARG REBUILD=unknown
ARG VERSION
ARG HYPERFIDDLE_DOMAIN
RUN clojure -X:build:prod:$HYPERFIDDLE_DOMAIN build-client :build $HYPERFIDDLE_DOMAIN :version '"'$VERSION'"' :debug true

ENV VERSION=$VERSION
CMD clojure -J-DHYPERFIDDLE_ELECTRIC_SERVER_VERSION='"'$VERSION'"' -M:prod:hello-fiddle -m prod domain hello-fiddle