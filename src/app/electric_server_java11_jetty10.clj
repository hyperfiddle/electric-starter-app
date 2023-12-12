(ns app.electric-server-java11-jetty10
  "preferred entrypoint (cleaner middleware for integration) but no java 8 compat"
  (:require [clojure.java.io :as io]
            [hyperfiddle.electric-jetty-adapter :as adapter]
            [clojure.tools.logging :as log]
            [ring.adapter.jetty9 :as ring]
            [ring.middleware.basic-authentication :as auth]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.cookies :as cookies]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :as res]
            [clojure.string :as str]
            [clojure.edn :as edn])
  (:import [java.io IOException]
           [java.net BindException]
           [org.eclipse.jetty.server.handler.gzip GzipHandler]))

(defn authenticate [username password] username) ; demo (accept-all) authentication

(defn wrap-demo-authentication "A Basic Auth example. Accepts any username/password and store the username in a cookie."
  [next-handler]
  (-> (fn [ring-req]
        (let [res (next-handler ring-req)]
          (if-let [username (:basic-authentication ring-req)]
            (res/set-cookie res "username" username {:http-only true})
            res)))
    (cookies/wrap-cookies)
    (auth/wrap-basic-authentication authenticate)))

(defn wrap-demo-router "A basic path-based routing middleware"
  [next-handler]
  (fn [ring-req]
    (case (:uri ring-req)
      "/auth" (let [response  ((wrap-demo-authentication next-handler) ring-req)]
                (if (= 401 (:status response)) ; authenticated?
                  response                     ; send response to trigger auth prompt
                  (-> (res/status response 302) ; redirect
                    (res/header "Location" (get-in ring-req [:headers "referer"]))))) ; redirect to where the auth originated
      ;; For any other route, delegate to next middleware
      (next-handler ring-req))))

(defn template "Takes a `string` and a map of key-values `kvs`. Replace all instances of `$key$` by value in `string`"
  [string kvs]
  (reduce-kv (fn [r k v] (str/replace r (str "$" k "$") v)) string kvs))

(defn get-modules [manifest-path]
  (when-let [manifest (io/resource manifest-path)]
    (let [manifest-folder (when-let [folder-name (second (rseq (str/split manifest-path #"\/")))]
                            (str "/" folder-name "/"))]
      (->> (slurp manifest)
        (edn/read-string)
        (reduce (fn [r module] (assoc r (keyword "hyperfiddle.client.module" (name (:name module))) (str manifest-folder (:output-name module)))) {})))))

(defn wrap-index-page
  "Server the `index.html` file with injected javascript modules from `manifest.edn`. `manifest.edn` is generated by the client build and contains javascript modules information."
  [next-handler resources-path manifest-path]
  (fn [ring-req]
    (if-let [response (res/resource-response (str resources-path "/index.html"))]
      (if-let [modules (get-modules manifest-path)]
        (-> (res/response (template (slurp (:body response)) modules)) ; TODO cache in prod mode
          (res/content-type "text/html") ; ensure `index.html` is not cached
          (res/header "Cache-Control" "no-store")
          (res/header "Last-Modified" (get-in response [:headers "Last-Modified"])))
        ;; No manifest found, can't inject js modules
        (-> (res/not-found "Missing client program manifest")
          (res/content-type "text/plain")))
      ;; index.html file not found on classpath
      (next-handler ring-req))))

(def ^:const VERSION (not-empty (System/getProperty "HYPERFIDDLE_ELECTRIC_SERVER_VERSION"))) ; see Dockerfile

(defn wrap-reject-stale-client
  "Intercept websocket UPGRADE request and check if client and server versions matches.
  An electric client is allowed to connect if its version matches the server's version, or if the server doesn't have a version set (dev mode).
  Otherwise, the client connection is rejected gracefully."
  [next-handler]
  (fn [ring-req]
    (if (ring/ws-upgrade-request? ring-req)
      (let [client-version (get-in ring-req [:query-params "HYPERFIDDLE_ELECTRIC_CLIENT_VERSION"])]
        (cond
          (nil? VERSION)             (next-handler ring-req)
          (= client-version VERSION) (next-handler ring-req)
          :else (ring/ws-upgrade-response (adapter/reject-websocket-handler 1008 "stale client")) ; https://www.rfc-editor.org/rfc/rfc6455#section-7.4.1
          ))
      (next-handler ring-req))))

(defn wrap-electric-websocket [next-handler]
  (fn [ring-request]
    (if (ring/ws-upgrade-request? ring-request)
      (let [authenticated-request    (auth/basic-authentication-request ring-request authenticate) ; optional
            electric-message-handler (partial adapter/electric-ws-message-handler authenticated-request)] ; takes the ring request as first arg - makes it available to electric program
        (ring/ws-upgrade-response (adapter/electric-ws-adapter electric-message-handler)))
      (next-handler ring-request))))

(defn electric-websocket-middleware [next-handler]
  (-> (wrap-electric-websocket next-handler) ; 4. connect electric client
    (cookies/wrap-cookies) ; 3. makes cookies available to Electric app
    (wrap-reject-stale-client) ; 2. reject stale electric client
    (wrap-params) ; 1. parse query params
    ))

(defn not-found-handler [_ring-request]
  (-> (res/not-found "Not found")
    (res/content-type "text/plain")))

(defn http-middleware [resources-path manifest-path]
  ;; these compose as functions, so are applied bottom up
  (-> not-found-handler
    (wrap-index-page resources-path manifest-path) ; 5. otherwise fallback to default page file
    (wrap-resource resources-path) ; 4. serve static file from classpath
    (wrap-content-type) ; 3. detect content (e.g. for index.html)
    (wrap-demo-router) ; 2. route
    (electric-websocket-middleware) ; 1. intercept electric websocket
    ))

(defn- add-gzip-handler
  "Makes Jetty server compress responses. Optional but recommended."
  [server]
  (.setHandler server
    (doto (GzipHandler.)
      #_(.setIncludedMimeTypes (into-array ["text/css" "text/plain" "text/javascript" "application/javascript" "application/json" "image/svg+xml"])) ; only compress these
      (.setMinGzipSize 1024)
      (.setHandler (.getHandler server)))))

(defn start-server! [{:keys [port resources-path manifest-path]
                                   :or   {port            8080
                                          resources-path "public"
                                          manifest-path  "public/js/manifest.edn"}
                                   :as   config}]
  (try
    (let [server (ring/run-jetty (http-middleware resources-path manifest-path)
                   (merge {:port port
                           :join? false
                           :configurator add-gzip-handler}
                     config))
          final-port (-> server (.getConnectors) first (.getPort))]
      (println "\n👉 App server available at" (str "http://" (:host config) ":" final-port "\n"))
      server)

    (catch IOException err
      (if (instance? BindException (ex-cause err))  ; port is already taken, retry with another one
        (do (log/warn "Port" port "was not available, retrying with" (inc port))
            (start-server! (update config :port inc)))
        (throw err)))))

