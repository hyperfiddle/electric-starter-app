(ns electric-fiddle.server-jetty
  (:require
   [clojure.tools.logging :as log]
   [electric-fiddle.ring-middleware :as middleware]
   [hyperfiddle.electric-ring-adapter :as electric-ring]
   [ring.adapter.jetty :as ring]
   [ring.middleware.cookies :as cookies]
   [ring.middleware.params :refer [wrap-params]])
  (:import
   (java.io IOException)
   (java.net BindException)
   (org.eclipse.jetty.server.handler.gzip GzipHandler)
   (org.eclipse.jetty.websocket.server.config JettyWebSocketServletContainerInitializer JettyWebSocketServletContainerInitializer$Configurator)))

(defn electric-websocket-middleware
  "Open a websocket and boot an Electric server program defined by `entrypoint`.
  Takes:
  - a ring handler `next-handler` to call if the request is not a websocket upgrade (e.g. the next middleware in the chain),
  - a `config` map eventually containing {:hyperfiddle.electric/user-version <version>} to ensure client and server share the same version,
    - see `hyperfiddle.electric-ring-adapter/wrap-reject-stale-client`
  - an Electric `entrypoint`: a function (fn [ring-request] (e/boot-server {} my-ns/My-e-defn ring-request))
  "
  [next-handler config entrypoint]
  ;; Applied bottom-up
  (-> (electric-ring/wrap-electric-websocket next-handler entrypoint) ; 5. connect electric client
    (middleware/wrap-authenticated-request) ; 4. Optional - authenticate before opening a websocket
    (cookies/wrap-cookies) ; 3. makes cookies available to Electric app
    (electric-ring/wrap-reject-stale-client config) ; 2. reject stale electric client
    (wrap-params))) ; 1. parse query params

(defn middleware [config entrypoint]
  (-> (middleware/http-middleware config)  ; 2. serve regular http content
    (electric-websocket-middleware config entrypoint))) ; 1. intercept electric websocket

(defn- add-gzip-handler!
  "Makes Jetty server compress responses. Optional but recommended."
  [server]
  (.setHandler server
    (doto (GzipHandler.)
      #_(.setIncludedMimeTypes (into-array ["text/css" "text/plain" "text/javascript" "application/javascript" "application/json" "image/svg+xml"])) ; only compress these
      (.setMinGzipSize 1024)
      (.setHandler (.getHandler server)))))

(defn- configure-websocket!
  "Tune Jetty Websocket config for Electric compat." [server]
  (JettyWebSocketServletContainerInitializer/configure
    (.getHandler server)
    (reify JettyWebSocketServletContainerInitializer$Configurator
      (accept [_this _servletContext wsContainer]
        (.setIdleTimeout wsContainer (java.time.Duration/ofSeconds 60))
        (.setMaxBinaryMessageSize wsContainer (* 100 1024 1024)) ; 100M - temporary
        (.setMaxTextMessageSize wsContainer (* 100 1024 1024))   ; 100M - temporary
        ))))

(defn start-server! [entrypoint
                     {:keys [port host]
                      :or   {port 8080, host "0.0.0.0"} ; insecure default?
                      :as   config}]
  (log/info (pr-str config))
  (try
    (let [server     (ring/run-jetty (middleware config entrypoint)
                       (merge {:port         port
                               :join?        false
                               :configurator (fn [server]
                                               (configure-websocket! server)
                                               (add-gzip-handler! server))}
                         config))
          final-port (-> server (.getConnectors) first (.getPort))]
      (log/info "👉" (str "http://" host ":" final-port))
      server)

    (catch IOException err
      (if (instance? BindException (ex-cause err))  ; port is already taken, retry with another one
        (do (log/warn "Port" port "was not available, retrying with" (inc port))
            (start-server! entrypoint (update config :port inc)))
        (throw err)))))
