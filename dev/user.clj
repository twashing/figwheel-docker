(ns user
  (:require
   [figwheel-sidecar.repl-api :as f]
   [ring.adapter.jetty :as jetty]
   [compojure.core :refer :all]
   [compojure.route :as route]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [ring.util.response :as response]))

;; user is a namespace that the Clojure runtime looks for and
;; loads if its available

;; You can place helper functions in here. This is great for starting
;; and stopping your webserver and other development services

;; The definitions in here will be available if you run "lein repl" or launch a
;; Clojure repl some other way

;; You have to ensure that the libraries you :require are listed in your dependencies

;; Once you start down this path
;; you will probably want to look at
;; tools.namespace https://github.com/clojure/tools.namespace
;; and Component https://github.com/stuartsierra/component


(defonce dev-state (atom {}))

(defroutes app-routes
  ;; NOTE: this will deliver all of your assets from the public directory
  ;; of resources i.e. resources/public
  (route/resources "/" {:root "public"})
  ;; NOTE: this will deliver your index.html
  (GET "/" [] (-> (response/resource-response "index.html" {:root "public"})
                  (response/content-type "text/html")))
  (GET "/hello" [] "Hello World!")
  (route/not-found "Not Found"))

(def app (wrap-defaults app-routes site-defaults))

;; this development application has a var reference to the app-routes above
;; for friendlier REPL based reloading
(def dev-app (wrap-defaults #'app-routes site-defaults))


;; ===

(defn start-server
  "Starts a ring server for your developement application"
  []
  (if-not (:ring-server @dev-state)
    (swap! dev-state assoc :ring-server

           ;; NOTE using var for better REPL reloading dev experience
           ;; (ring-server/serve #'server-handler/dev-app {:open-browser? false})
           (jetty/run-jetty #'dev-app {:port 8080}))
    (println "Server already running!")))

(defn stop-server
  "Stops the running ring server."
  []
  (when-let [ring-server (:ring-server @dev-state)]
    (swap! dev-state dissoc :ring-server)
    (println "Stopping ring server!")
    (.stop ring-server)))


;; ===

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
