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
ARG HYPERFIDDLE_DOMAIN
ARG ELECTRIC_USER_VERSION
RUN clojure -A:prod:$HYPERFIDDLE_DOMAIN -M -e ::ok         # preload
RUN clojure -A:build:prod:$HYPERFIDDLE_DOMAIN -M -e ::ok   # preload
RUN clojure -X:build:prod:$HYPERFIDDLE_DOMAIN uberjar \
    :hyperfiddle/domain $HYPERFIDDLE_DOMAIN \
    :build/jar-name user.jar

CMD java -cp user.jar clojure.main -m prod