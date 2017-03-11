(ns my-compojure-app.handler
  (:use [ring.middleware defaults params multipart-params keyword-params nested-params json])
  (:require [compojure.core :refer :all]
            [compojure.route :as route]

            [org.httpkit.server :as http-server]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:gen-class))

(defroutes app-routes
  (GET "/" [] "Hello, world!")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))


;; Capture the result, which is a function used to stop the server.
#_  (def server-stop-fn (http-server/run-server app {:port 9001}))

#_  (server-stop-fn)
