(ns shelfshare.goodreads
  (:require [oauth.client :as oauth]))


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

(def request-token 
  (oauth/request-token goodreads-consumer))

(def auth-req 
  (oauth/user-approval-uri goodreads-consumer 
                           (:oauth_token request-token)))

(def goodreads-token "YIWFKqRUBHoHjHqYHpthA")



