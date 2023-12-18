(ns fiddle-manager
  (:require
   [clojure.pprint :as pprint]
   [hyperfiddle.electric :as e]
   [hyperfiddle.electric-local-def :as local]
   [clojure.string :as str]
   [hyperfiddle.rcf :as rcf])
  (:import
   (hyperfiddle.electric Pending)
   (missionary Cancelled)))

(defonce !FIDDLES ; "Contains the set of loaded fiddles, by namespace symbols."
  (atom #{}))

(defn fiddle-entrypoint-ns [fiddle-sym] (symbol (str (name fiddle-sym) ".fiddles")))
(defn fiddle-entrypoint [fiddle-sym] (symbol (str (fiddle-entrypoint-ns fiddle-sym)) "fiddles"))

(defn load-fiddle! [& ns-syms]
  (assert (every? simple-symbol? ns-syms))
  (swap! !FIDDLES into ns-syms))

(defn unload-fiddle! [ns-sym] (swap! !FIDDLES disj ns-sym))

(defn require-fiddle [fiddle]
  (let [ns-sym (fiddle-entrypoint-ns fiddle)]
    (println "Loading fiddle:" fiddle)
    (let [rcf-state rcf/*enabled*]
      (try (rcf/enable! false)
           (require ns-sym :reload)
           (println "Loaded:" ns-sym)
           (find-ns ns-sym)
           (catch Throwable t
             (println "Fiddle manager failed to load fiddle" fiddle (ex-message t))
             (unload-fiddle! fiddle)
             nil)
           (finally
             (rcf/enable! rcf-state))))))



(def default-cljs-loader-ns '(ns cljs-fiddles-loader))

(defn gen-ns-form [fiddles]
  (list 'ns 'fiddles 
    `(:require ~'[hyperfiddle.electric :as e]
               ~'[hyperfiddle :as hf]
               ~'electric-fiddle.main
               ~@(map fiddle-entrypoint-ns fiddles))))

(defn gen-index-form [fiddles]
  `(~'e/def ~'fiddles (merge ~@(map fiddle-entrypoint fiddles))))

(defn gen-entrypoint []
  '(e/defn FiddleMain [ring-req]
     (e/client
       (binding [hf/pages fiddles]
         (e/server
           (electric-fiddle.main/Main. ring-req))))))

(defn add-require-macros [ns-form-str]
  (str/replace ns-form-str #"\)\n$" "\n  #?(:cljs (:require-macros [fiddles])))\n"))

(with-out-str
  (pprint/with-pprint-dispatch pprint/code-dispatch
    (pprint/pprint '(ns foo (:require bar baz baz baz)))))

(defn write-loader-file
  ([path] (write-loader-file path ()))
  ([path fiddles]
   (spit path
     (with-out-str ; Couldn't make it work with (with-open [*out* (io/writer …)] …)
       (println ";; DO NOT EDIT MANUALLY")
       (println ";; This file is generated and managed by Electric Fiddle")
       (println ";; Use `dev/load-fiddle!`, `dev/unload-fiddle!` at the REPL or edit your fiddle configuration.")
       (println ";; Use `dev/load-fiddle!` and `dev/unload-fiddle!` instead.")
       (println)
       (println ";; This file reflects the state of loaded fiddles, from the REPL or from config.")
       (println ";; At the REPL, shadow will recompile and reload this file whenever it changes, driven by Electric Fiddle.")
       (println)
       (println ";; For git to ignore/track this file run `git update-index --skip-worktree | --no-skip-worktree src-dev/fiddles.cljc`.")
       (pprint/with-pprint-dispatch pprint/code-dispatch
         (print (add-require-macros (with-out-str (pprint/pprint (gen-ns-form fiddles)))))
         (println)
         (pprint/pprint (gen-index-form fiddles))
         (println)
         (pprint/pprint (gen-entrypoint)))))))

(comment
  (write-loader-file "./src-dev/fiddles.cljc" '(hello-fiddle))
  )

(local/defn FiddleLoader [path fiddles]
  (e/server
    (let [loaded-fiddles (e/for-by identity [fiddle fiddles]
                           (try
                             (e/offload #(require-fiddle fiddle))
                             (e/on-unmount #(do (println "Unloading fiddle:" fiddle)
                                                (remove-ns (fiddle-entrypoint-ns fiddle))))
                             fiddle
                             (catch hyperfiddle.electric.Pending _
                               false)))]
      (when (not-any? false? loaded-fiddles)
        (write-loader-file path fiddles)))
    (e/on-unmount #(write-loader-file path ()))))


(local/defn FiddleManager [{:keys [loader-path] :as _config}]
  (try
    (e/server
      (let [fiddles (e/watch !FIDDLES)]
        (FiddleLoader. loader-path fiddles)))
    (catch Pending _)
    (catch Cancelled _)
    (catch Throwable t
      (prn "Fiddle Manager crashed" t))))

(def manager nil)

(def DEFAULT-CONFIG {:loader-path "src-dev/fiddles.cljc"})

(defn start!
  ([] (start! nil))
  ([config]
   (when (nil? manager)
     (def manager (local/run (FiddleManager. config))))))

(defn stop! []
  (when (some? manager)
    (manager)
    (def manager nil)))

(comment
  (start!)
  (load-fiddle! 'hello-fiddle)
  (unload-fiddle! 'hello-fiddle)
  (load-fiddle! 'i-dont-exist)
  (stop!)
  )


(comment
  ;; Failed experiment to inject requires in an ns as a macro
  (defmacro loaded-fiddles [] (vec (map fiddle-entrypoint @!FIDDLES)))

  (defn requires [ns-tail]
    (rest (first (filter #(= :require (first %)) ns-tail))))

  (defmacro gen-ns [ns & body]
    (let [requires (concat (requires body) (map fiddle-entrypoint-ns @!FIDDLES))]
      `(~'ns ~ns ~@body
        ~@(when-some [reqs (seq requires)] `[(:require ~@reqs)]))))
  )
