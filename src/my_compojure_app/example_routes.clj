(ns my-compojure-app.example-routes
  (:use [ring.middleware defaults params multipart-params keyword-params nested-params json])
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [response]]
            [hiccup.core :refer [html]]
            [org.httpkit.server :as http-server]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))



;;;; The Simplest Example

(defroutes app-routes
  (GET "/" [] "Hello,  world!")
  (route/not-found "Not Found"))


;;;; Order doesn't matter

(defroutes app-routes
  (GET "/hello"   [] "Hello,  world!")
  (GET "/goodbye" [] "Goodbye,  world!")
  (route/not-found "Not Found"))

;; Or

(defroutes app-routes
  (GET "/goodbye" [] "Goodbye,  world!")
  (GET "/hello"   [] "Hello,  world!")
  (route/not-found "Not Found"))



;;;; Or does it?

(defroutes app-routes
  (GET "/hello/:foo"   [] "Hello,  world!") ; Oh darn.
  (GET "/hello/there"  [] "Hello there,  world!"))

;; You would never do that, right?


;;;; Composing routes in a larger app

(defroutes app-routes                   ; Are these correct?
  module-a/some-routes                  ; Can you tell by looking at them?
  module-b/some-other-routes            ; No?  Neither can I!
  module-c/yet-more-routes)


;;;;  Handlers

(defn debugging-handler [request]
  {:status 200
   :body   (with-out-str
             (clojure.pprint/pprint
              (select-keys request [:params :route-params :compojure/route :path-info :uri])))})

(defroutes app-routes
  (POST "/debug" [] debugging-handler))


;;;;  Routes destructuring

(defn register-user-handler [name mobile]
  {:status 201
   :body   (format "Name was \"%s\" and mobile was \"%s\" " name mobile)})

(defroutes app-routes
  ;; Destructuring the so called ":route-params"
  (POST "/register-user/:name/:mobile" [name mobile] (register-user-handler name mobile)))

(defroutes app-routes
  ;; Or you can destructuring the form params
  (POST "/register-user" [name mobile] (register-user-handler name mobile)))















;;;; Interlude: Middlewares

;;; Functions that sit "in the middle" between the web server and your handler

;; An example "input middleware"
(fn [handler]
  (fn [request]
    (let [modified-request (munge-request request)]
      (handler modified-request))))

;; An example "output middleware"
(fn [handler]
  (fn [request]
    (let [response (handler request)]
      (munge-response response))))

;; Note that a middleware is nothing more than a function which creates a handler (fn).











;;;; Trivial middlewares

(def *users*
  {"tiger" {:user "Tiger Woods" :age 41 :interests ["Golf" "Women"]}
   "shane" {:user "Shane Warne" :age 46 :interests ["Cricket" "Beer"]}
   "alain" {:user "Alain Picard" :age "None of your business"
            :interests ["Clojure" "Lisp" "Scotch Whisky"]}})

(def find-user-middleware
  (fn [handler]
    (fn [request]
      (let [user-id (get-in request [:route-params :user-id])
            user    (get *users* user-id "No such user")]
        (handler (assoc request :user user))))))

(defn show-user-handler [request]
  (response (format "We got user %s" (:user request))))

(defroutes app-routes
  (context "/blah" []
    (GET "/user/:user-id" [user-id] (-> show-user-handler
                                        find-user-middleware))))







;;;; Supplied middlewares:
;;;
;;;  More than you can shake a stick at
;;;

(defn wrap-defaults
  "Wraps a handler in default Ring middleware, as specified by the supplied
  configuration map.

  See: api-defaults
       site-defaults
       secure-api-defaults
       secure-site-defaults"
  [handler config]
  (-> handler
      (wrap wrap-anti-forgery     (get-in config [:security :anti-forgery] false))
      (wrap wrap-flash            (get-in config [:session :flash] false))
      (wrap wrap-session          (:session config false))
      (wrap wrap-keyword-params   (get-in config [:params :keywordize] false))
      (wrap wrap-nested-params    (get-in config [:params :nested] false))
      (wrap wrap-multipart-params (get-in config [:params :multipart] false))
      (wrap wrap-params           (get-in config [:params :urlencoded] false))
      (wrap wrap-cookies          (get-in config [:cookies] false))
      (wrap wrap-absolute-redirects (get-in config [:responses :absolute-redirects] false))
      (wrap wrap-resource         (get-in config [:static :resources] false))
      (wrap wrap-file             (get-in config [:static :files] false))
      (wrap wrap-content-type     (get-in config [:responses :content-types] false))
      (wrap wrap-default-charset  (get-in config [:responses :default-charset] false))
      (wrap wrap-not-modified     (get-in config [:responses :not-modified-responses] false))
      (wrap wrap-x-headers        (:security config))
      (wrap wrap-hsts             (get-in config [:security :hsts] false))
      (wrap wrap-ssl-redirect     (get-in config [:security :ssl-redirect] false))
      (wrap wrap-forwarded-scheme      (boolean (:proxy config)))
      (wrap wrap-forwarded-remote-addr (boolean (:proxy config)))))



;; Examples of a real world set of middleware wrappers at GoCatch

(defn v2-routes-wrapper [routes]
  (-> routes
      (mw/wrap-request-logging :inside)
      wrap-vehicle

      ;; IMPORTANT! Read carefully and understand!
      ;; There is an implicit throw/catch protocol between the handlers,
      ;; which call things like (redirect), (gone)  etc. and the standard-http-response
      ;; handler.  However, things which add to the request "on the way out", like
      ;; wrap-outstanding-messages and wrap-device-location won't run if they are
      ;; inside that catch.  Make sure you don't add any middleware
      ;; above here which wants to munge outgoing responses and which must succeed
      ;; independent of the request!
      mw/wrap-standard-http-response

      wrap-device-location              ; INBOUND - adds x-location
      wrap-outstanding-messages         ; OUTBOUND - adds x-gocatch-messages -- needs auth to have run

      (mw/wrap-api-authentication :rest) ; INBOUND: adds :account-id (or bails)
      (ring.middleware.json/wrap-json-body {:keywords? ->kebab-case-keyword}) ; INBOUND: decodes body into json map
      wrap-multipart-params ; standard wrappers
      wrap-nested-params
      wrap-keyword-params
      wrap-params

      ;; "OUTBOUND" handlers
      ;; These munge the outgoing repsonse with extra headers, etc.
      wrap-json-response
      wrap-not-modified
      wrap-do-not-cache
      (mw/wrap-request-logging :outside)))





;;;;                        Are we confused yet?
;;;;
;;;;
;;;;                     
;;;;                         it gets worse!!!
;;;;
;;;;
;;;;                     
;;;;                         Back to routing 


















;;                    The all-important CONTEXT macro
;;                    -------------------------------

;; Remember this guy?
(defroutes app-routes                   ; Are these correct?
  module-a/some-routes                  ; Can you tell by looking at them?
  module-b/some-other-routes            ; No?  Neither can I!
  module-c/yet-more-routes)

;; Best rewritten like this:
(defroutes app-routes       
  (context "/module-a" [] module-a/some-routes)      
  (context "/module-b" [] module-b/some-other-routes)
  (context "/module-c" [] module-c/yet-more-routes))



 




;;
;;         This solves the problem... at the cost of forcing you to have
;;         complete up-front knowledge and final design of your entire URL space.
;;
;;
;; BUT...
;;         Does that sound like any app YOU've ever worked on???








;; * Handlers can return NIL to "pass"


;; * middlewares can fail to propagate (e.g. authentication)


;; * 
;; *
;; *
















;;;; The End
(defroutes app-routes
  (GET "/" [] "Goodbye,  world!")
  (route/not-found "Not Found"))
