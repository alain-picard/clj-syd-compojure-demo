(ns my-compojure-app.complex-handler
  (:use [ring.middleware defaults params multipart-params keyword-params nested-params json])
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [response]]
            [hiccup.core :refer [html]]
            [org.httpkit.server :as http-server]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]

            [my-compojure-app
             [module-a :as a]
             [module-b :as b]
             [module-c :as c]]))

(defn show-quotes-route [request]
  (response
   (html
    [:h1 "Quotes:  " (str (java.util.Date.))]
    (my-compojure-app.princess-bride/render-quotes-in-request request))))

(defroutes app-routes
  (context "/api/v1.0" []
    (routes
     (GET "/quotes" [] show-quotes-route)
     (context "/a" [] a/module-routes)
     (context "/b" [] b/module-routes)
     (context "/c" [] c/module-routes)))
  (route/not-found "Not Found"))

(def app (-> #'app-routes
             (my-compojure-app.princess-bride/vizzini "Vizzini")
             (my-compojure-app.princess-bride/fezzik "Fezzik")
             (my-compojure-app.princess-bride/inigo "Inigo")))



;; Capture the result, which is a function used to stop the server.
#_  (def server-stop-fn (http-server/run-server #'app {:port 9001}))

#_  (server-stop-fn)
