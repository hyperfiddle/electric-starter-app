FROM clojure:openjdk-11-tools-deps AS clojure-deps
WORKDIR /app
COPY deps.edn deps.edn
COPY src-build src-build
RUN clojure -A:dev -M -e :ok        # preload deps
RUN clojure -T:build noop           # preload build deps

FROM clojure:openjdk-11-tools-deps AS build
WORKDIR /app
COPY --from=clojure-deps /root/.m2 /root/.m2
COPY shadow-cljs.edn shadow-cljs.edn
COPY deps.edn deps.edn
COPY src src
COPY src-build src-build
COPY resources resources
ARG REBUILD=unknown
ARG VERSION
RUN clojure -X:build uberjar :jar-name "app.jar" :verbose true :version '"'$VERSION'"'

FROM amazoncorretto:11 AS app
WORKDIR /app
COPY --from=build /app/app.jar app.jar
EXPOSE 8080
ARG VERSION
ENV VERSION=$VERSION
CMD java -DHYPERFIDDLE_ELECTRIC_SERVER_VERSION=$VERSION -jar app.jar
