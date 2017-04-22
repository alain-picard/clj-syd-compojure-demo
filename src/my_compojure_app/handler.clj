(ns my-compojure-app.handler
  (:use [ring.middleware defaults params multipart-params keyword-params nested-params json])
  (:require [compojure.core :refer :all]
            [compojure.route :as route]

            [org.httpkit.server :as http-server]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [my-compojure-app.example-routes :as example])
  (:gen-class))



(def app
  (wrap-defaults #'example/app-routes
                 {:params    {:urlencoded true
                              :multipart  true
                              :nested     true
                              :keywordize true}
                  :cookies   true}))


;; Capture the result, which is a function used to stop the server.
#_  (def server-stop-fn (http-server/run-server app {:port 9001}))

#_  (server-stop-fn)


(defn -main []
  ;; Purists tell you to not do this.  :-)
  (def server-stop-fn
    "The callback allowing us to shut this thing off, if need be."
    (http-server/run-server app {:port 9001})))
