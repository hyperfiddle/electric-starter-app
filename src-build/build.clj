(ns build
  (:require [clojure.tools.build.api :as b]
            [shadow.cljs.devtools.api :as shadow-api]
            [shadow.cljs.devtools.server :as shadow-server]))

;(def lib 'com.hyperfiddle/electric-fiddle)
(def version (b/git-process {:git-args "describe --tags --long --always --dirty"}))
;(def basis (b/create-basis {:project "deps.edn" :aliases [:prod]}))
;(def class-dir "target/classes")
;(def defaults {:src-pom "src-build/pom-template.xml" :lib lib})

(defn build-client
  "build Electric app client. note: in-process shadow compilation requires 
application classpath to be available, i.e. `clj -X:build` not `clj -T:build`"
  ; No point in sheltering shadow from app classpath, shadow loads it anyway!
  ; Simply fix the tools.build conflict by excluding guava-android
  [{:keys [optimize debug verbose version]
    :or {optimize true, debug false, verbose false, version version}}]
  (println "Building client, version:" version)
  (b/delete {:path "resources/public/js"})
  ; "java.lang.NoClassDefFoundError: com/google/common/collect/Streams" is fixed by
  ; adding com.google.guava/guava {:mvn/version "31.1-jre"} to deps, 
  ; see https://hf-inc.slack.com/archives/C04TBSDFAM6/p1692636958361199
  (shadow-server/start!)
  (shadow-api/release :prod
    {:debug debug,
     :verbose verbose,
     :config-merge
     [{:compiler-options {:optimizations (if optimize :advanced :simple)}
       :closure-defines {'hyperfiddle.electric-client/VERSION version}}]})
  (shadow-server/stop!))

; TODO: Bake the git version into the index.html
; TODO: Bake the electric version into the prod artifact
; Do we care about uberjar?
