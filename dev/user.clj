(ns user
  (:require [finops-admin.server]
            [ring.middleware.reload :refer [wrap-reload]]
            [figwheel-sidecar.repl-api :as f]))

;; Let Clojure warn you when it needs to reflect on types, or when it does math
;; on unboxed numbers. In both cases you should add type annotations to prevent
;; degraded performance.
(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
(def http-handler
  (wrap-reload #'finops-admin.server/http-handler))

(defn fig-start
    "This starts the figwheel server and watch based auto-compiler."
    []
    ;; this call will only work are long as your :cljsbuild and
    ;; :figwheel configurations are at the top level of your project.clj
    ;; and are not spread across different lein profiles

    ;; otherwise you can pass a configuration into start-figwheel! manually
    (f/start-figwheel!))

(defn fig-stop
    "Stop the figwheel server and watch based auto-compiler."
    []
    (f/stop-figwheel!))

;; if you are in an nREPL environment you will need to make sure you
;; have setup piggieback for this to work
(defn cljs-repl
    "Launch a ClojureScript REPL that is connected to your build and host environment."
    []
    (f/cljs-repl))
