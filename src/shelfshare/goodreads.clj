(ns shelfshare.goodreads
  (:require 
    [clj-http.client :as http]
    [schema.core :as s]
    [schema.macros :as sm]
    [clojure.xml :as xml]
    [clojure.zip :as zip]
    [clojure.data.zip.xml :as data ]
    [oauth.client :as oauth]))

(def Oauth-token
  {:oauth_token s/Str
   :oauth_token_secret s/Str})

(def Method
   (s/enum :POST :GET))


(def consumer-key    "anl0o79B3ovjLYCNRxDEA")
(def consumer-secret "tyCyi5TqNA94CheAPIS5Pk2iGLQXa9VWmwaJomoFS8s")
(def callback-uri    "https://shelfshare.herokuapp.com/oauth")

(def goodreads-uri "http://www.goodreads.com/oauth/")
(def goodreads-consumer (oauth/make-consumer 
                          consumer-key
                          consumer-secret
                          (str goodreads-uri "request_token")
                          (str goodreads-uri "access_token")
                          (str goodreads-uri "authorize") 
                          :hmac-sha1))

(defn request-token [] 
  (oauth/request-token goodreads-consumer))

(defn auth-req [request-token] 
  (oauth/user-approval-uri goodreads-consumer 
                           (:oauth_token request-token)))

(defn access-token [request-token]
  (oauth/access-token goodreads-consumer request-token))

(def goodreads-token "YIWFKqRUBHoHjHqYHpthA")

(def goodreads-oauth {:oauth_token goodreads-token
                      :oauth_token_secret request-token})

(sm/defn creds [path :- s/Str  
                method :- Method 
                payload :- s/Any 
                token :- Oauth-token]
  (oauth/credentials goodreads-consumer
                     (:oauth_token token)
                     (:oauth_token_secret token)
                      method 
                     (str "https://www.goodreads.com/" path)
                     payload))

(defn zip-str  [s]
    (zip/xml-zip  
      (xml/parse  
        (java.io.ByteArrayInputStream.  
          (.getBytes s)))))

(defn get-id [zipper]  
   (data/xml1-> zipper :user (data/attr :id)))

(defn get-id-xml [xml]
  (get-id (zip-str xml)))

(sm/defn auth-user [token :- Oauth-token]
  (-> 
    (http/get "https://www.goodreads.com/api/auth_user"
              {:query-params 
               (creds "api/auth_user" :GET {} token)})
      :body
      get-id-xml  
      ))

(sm/defn get-freinds [token :- Oauth-token id ]
  (-> 
    (http/get 
      (str "https://www.goodreads.com/friend/user/" 
           id "?format=xml")
              {:query-params 
               (creds 
                 (str "friend/user/" id) 
                 :GET {} token)})
      :body
      
      ))



