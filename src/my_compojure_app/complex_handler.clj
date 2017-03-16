(ns my-compojure-app.complex-handler
  (:use [ring.middleware defaults params multipart-params keyword-params nested-params json])
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :as http-server]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]

            [my-compojure-app
             [module-a :as a]
             [module-b :as b]
             [module-c :as c]]))

(defroutes app-routes
  (context "/api/v1.0" []
    (routes
     a/module-routes
     b/module-routes
     c/module-routes))
  (route/not-found "Not Found"))

(def app (-> #'app-routes                       ; Note: make it reloadable!
             (wrap-defaults api-defaults)))     ; Everybody wants this middleware.


;; Capture the result, which is a function used to stop the server.
#_  (def server-stop-fn (http-server/run-server #'app {:port 9001}))

#_  (server-stop-fn)
