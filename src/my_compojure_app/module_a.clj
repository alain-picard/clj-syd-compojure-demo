;;; Imagine these 3 chunks are all in their
;;; own files, perhaps being developed by different
;;; developers...

(ns my-compojure-app.module-a)
(compojure.core/defroutes module-routes
  (compojure.core/GET "/foo" [] (constantly {:status 200 :body "module A"})))


(ns my-compojure-app.module-b)
(compojure.core/defroutes module-routes
  (compojure.core/GET "/bar" [] (constantly {:status 200 :body "module B"}))
  ;; Bob adds this route
  (compojure.core/GET "/users" [] (constantly {:status 200 :body "module B Users"})))


(ns my-compojure-app.module-c)
(compojure.core/defroutes module-routes
  (compojure.core/GET "/baz" [] (constantly {:status 200 :body "module C"})))
