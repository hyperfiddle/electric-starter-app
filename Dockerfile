FROM node:14.7-stretch AS node-deps
WORKDIR /app
COPY package.json package.json
RUN npm install

FROM clojure:openjdk-11-tools-deps AS build
WORKDIR /app
COPY --from=node-deps /app/node_modules /app/node_modules

ARG DATOMIC_DEV_LOCAL_USER
ARG DATOMIC_DEV_LOCAL_PASSWORD
ENV DATOMIC_DEV_LOCAL_USER=$DATOMIC_DEV_LOCAL_USER
ENV DATOMIC_DEV_LOCAL_PASSWORD=$DATOMIC_DEV_LOCAL_PASSWORD

# Authorize SSH Host
RUN mkdir -p /root/.ssh && \
    chmod 0700 /root/.ssh && \
    ssh-keyscan github.com >> /root/.ssh/known_hosts

# Create a wrapper for clojure binary
ARG hfql_ssh_prv_key
# Private keys MUST be written to a file this way ("$var") and end with a newline
# If passed as a command arg directly, Github shell will strip newlines and corrupt it.
RUN echo "$hfql_ssh_prv_key" > /root/.ssh/id_rsa

RUN mkdir -p /usr/local/sbin/ && \
    echo -e '#!/bin/sh \n eval $(ssh-agent -s) && exec /usr/local/bin/clojure "$@"' >> /usr/local/sbin/clojure && \
    chmod +x /usr/local/sbin/clojure

COPY .m2 /root/.m2
COPY shadow-cljs.edn shadow-cljs.edn
COPY deps.edn deps.edn
COPY src src
COPY src-build src-build
COPY vendor vendor
COPY resources resources
ARG REBUILD=unknown
ARG VERSION
RUN clojure -T:build build-client :verbose true :version '"'$VERSION'"'

ENV VERSION=$VERSION
CMD clj -J-DHYPERFIDDLE_ELECTRIC_SERVER_VERSION=$VERSION -M -m prod
