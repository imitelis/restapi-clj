(defproject rest-api "0.1.0-SNAPSHOT"
  :description "Reitit REST API Swagger Clojure"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [metosin/reitit "0.5.5"]
                 [camel-snake-kebab "0.4.1"]]
  :repl-options {:init-ns rest-api.server})
