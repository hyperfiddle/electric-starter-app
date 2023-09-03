(ns build
  (:require [clojure.tools.logging :as log]
            [clojure.tools.build.api :as b]
            [contrib.assert :refer [check]]
            [hyperfiddle :as hf]
            [shadow.cljs.devtools.api :as shadow-api]
            [shadow.cljs.devtools.server :as shadow-server]))

(def user-version (b/git-process {:git-args "describe --tags --long --always --dirty"}))

(defn build-client
  "build Electric app client, invoke with -X. Note: Electric shadow compilation 
requires application classpath to be available, so use `clj -X:build` not `clj -T:build`"
  ; No point in sheltering shadow from app classpath, shadow loads it anyway!
  [argmap] ; invoke with -X
  (let [{:keys [::hf/domain optimize debug verbose]
         :or {optimize true, debug false, verbose false}
         :as config}
        (-> argmap 
          (update ::hf/domain str) ; coerce, -X under bash evals as symbol unless shell quoted like '"'foo'"'
          (assoc :hyperfiddle.electric/user-version user-version))]
    (b/delete {:path "resources/public/js"})
    (b/delete {:path "resources/electric-manifest.edn"})
    
    ; bake domain and user-version into artifact, cljs and clj
    (b/write-file {:path "resources/electric-manifest.edn" :content config}) ; even used?
    
    ; "java.lang.NoClassDefFoundError: com/google/common/collect/Streams" is fixed by
    ; adding com.google.guava/guava {:mvn/version "31.1-jre"} to deps, 
    ; see https://hf-inc.slack.com/archives/C04TBSDFAM6/p1692636958361199
    (shadow-server/start!)
    (binding [hf/*hyperfiddle-user-ns* (symbol (str (name (check string? domain)) ".fiddles"))]
      (shadow-api/release :prod
        {:debug debug,
         :verbose verbose,
         :config-merge
         [{:compiler-options {:optimizations (if optimize :advanced :simple)}
    ; todo fail build on error
           :closure-defines {'hyperfiddle.electric-client/ELECTRIC_USER_VERSION user-version}}]}))
    (shadow-server/stop!)
    (log/info domain "client built")))

(def class-dir "target/classes")

(defn uberjar
  [{:keys [::hf/domain optimize debug verbose]
    :or {optimize true, debug false, verbose false}
    :as args}]
  (log/info `uberjar (pr-str args)) 
  (b/delete {:path "target"})
  
  (build-client {::hf/domain (check some? domain)
                 :optimize optimize, :debug debug, :verbose verbose})
  
  (b/copy-dir {:target-dir class-dir :src-dirs ["src" "src-prod" "resources"]})
  (let [jar-name (format "target/electricfiddle-%s-%s.jar" domain user-version)]
    (b/uber {:class-dir class-dir
             :uber-file jar-name
             :basis     (b/create-basis {:project "deps.edn" :aliases [:prod]})})
    (log/info jar-name)))

; clojure -A:prod:hello-fiddle -M -e ::ok 
; clojure -A:build:prod:hello-fiddle -M -e ::ok 
; clojure -X:build:prod:hello-fiddle build-client :hyperfiddle/domain hello-fiddle :debug true
; java -cp target/electricfiddle-hello-fiddle-77ebb18-dirty.jar clojure.main -m prod