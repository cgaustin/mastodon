(defproject lcmap-mastodon "5.0.0"
  :description "Functions for LCMAP data curation"
  :url "https://eros.usgs.gov"
  :license {:name "Unlicense"
            :url ""}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure       "1.9.0"]
                 [org.clojure/core.async    "0.3.443"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.slf4j/slf4j-log4j12   "1.7.21"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]
                 [cheshire                  "5.8.0"]
                 [clj-glob                  "1.0.0"]
                 [compojure                 "1.6.0"]
                 [environ                   "1.1.0"]
                 [http-kit                  "2.2.0"]
                 [http-kit.fake             "0.2.1"]
                 [ring                      "1.6.3"]
                 [ring/ring-defaults        "0.3.1"]
                 [ring/ring-json            "0.4.0"]
                 [ring/ring-jetty-adapter   "1.6.3"]
                 [ring/ring-mock            "0.3.2"]]

  :codox {:output-path "docs"}
  :source-paths ["src"]

  :profiles {:dev {:plugins [[lein-codox "0.10.7"]]
                   :dependencies [[org.clojure/test.check "0.10.0-alpha3"]]
                   :source-paths ["src"]}
             :uberjar {:omit-source true
                       :aot :all}
             :test {:resource-paths ["test/mastodon/clj"]}} ;;profiles
  :main mastodon.main
)
