(ns my-compojure-app.module-a)
(compojure.core/defroutes module-routes
  (compojure.core/GET "/foo" [] (constantly {:status 200 :body "module A"})))

(ns my-compojure-app.module-b)
(compojure.core/defroutes module-routes
  (compojure.core/GET "/bar" [] (constantly {:status 200 :body "module B"})))


(ns my-compojure-app.module-c)
(compojure.core/defroutes module-routes
  (compojure.core/GET "/baz" [] (constantly {:status 200 :body "module C"})))
