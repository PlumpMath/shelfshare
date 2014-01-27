(defproject shelfshare "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://shelfshare.herokuapp.com"
  :license {:name "FIXME: choose"
            :url "http://example.com/FIXME"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.1"]
                 [hiccup "1.0.4"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [clj-oauth "1.4.1"]
                 [clj-http "0.7.8"] 
                 [ring/ring-devel "1.1.0"]
                 [ring-basic-authentication "1.0.1"]
                 [environ "0.2.1"]
                 [com.cemerick/drawbridge "0.0.6"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.8.10"]
            [environ/environ.lein "0.2.1"]]
  :hooks [environ.leiningen.hooks]
  :ring  {:handler shelfshare.web/app}
  :profiles {:production {:env {:production true}}})
