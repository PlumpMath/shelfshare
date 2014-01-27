(ns shelfshare.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [hiccup.page :as page]
            [shelfshare.goodreads :as goodreads]
            [clojure.java.io :as io]
            [ring.middleware.stacktrace :as trace]
            [ring.middleware.session :as session]
            [ring.util.response :refer  [redirect response]]
            [ring.middleware.session.cookie :as cookie]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.basic-authentication :as basic]
            [cemerick.drawbridge :as drawbridge]
            [environ.core :refer [env]]))

(defn- authenticated? [user pass]
  (= [user pass] [(env :repl-user false) (env :repl-password false)]))

(def ^:private drawbridge
  (-> (drawbridge/ring-handler)
      (session/wrap-session)
      (basic/wrap-basic-authentication authenticated?)))
 
(defroutes app
  (ANY "/repl" {:as req}
       (drawbridge req))
  (GET "/" [] (let [req-tok (goodreads/request-token)]
               (assoc (redirect (goodreads/auth-req req-tok))
                   :session {:request-token req-tok})))

  (GET "/dex" {{:keys [access-token goodreads-id]} :session} 
       (response (str 
                   (goodreads/get-freinds 
                     access-token goodreads-id))))

  (GET "/oauth" {params :params session :session}
         (let [req-tok (:request-token session)
               access-tok (goodreads/access-token req-tok)
               gr-id   (goodreads/auth-user access-tok)]
           (condp = (:authorize params)
             "0" "401"
             "1" (assoc
                 (redirect "/dex")
                 :session 
                 {:access-token access-tok 
                  :goodreads-id gr-id 
                  }))))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn wrap-error-page [handler]
  (fn [req]
    (try (handler req)
         (catch Exception e
           {:status 500
            :headers {"Content-Type" "text/html"}
            :body (slurp (io/resource "500.html"))}))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))
        store (cookie/cookie-store {:key (env :session-secret)})]
    (jetty/run-jetty (-> #'app
                         ((if (env :production)
                            wrap-error-page
                            trace/wrap-stacktrace))
                         (site {:session {:store store}}))
                     {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;;(def server (-main))
