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
RUN mkdir -p /usr/local/sbin/ && \
    echo -e '#!/bin/sh \n eval $(ssh-agent -s) && ssh-add -k ~/.ssh/id_rsa && exec /usr/local/bin/clojure "$@"' >> /usr/local/sbin/clojure && \
    chmod +x /usr/local/sbin/clojure

ARG ssh_prv_key

# Add the keys and set permissions
RUN echo "$ssh_prv_key" > /root/.ssh/id_rsa && \
    chmod 600 /root/.ssh/id_rsa

COPY .m2 /root/.m2
COPY shadow-cljs.edn shadow-cljs.edn
COPY deps.edn deps.edn
COPY src src
COPY src-build src-build
COPY resources resources
ARG REBUILD=unknown
ARG VERSION
RUN clojure -T:build build-client :verbose true :version '"'$VERSION'"'

ENV VERSION=$VERSION
CMD clj -J-DHYPERFIDDLE_ELECTRIC_SERVER_VERSION=$VERSION -M -m prod
