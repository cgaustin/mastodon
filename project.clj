(defproject lcmap-mastodon "0.1.0-SNAPSHOT"
  :description "Functions for LCMAP data sources"
  :url "https://eros.usgs.gov"
  :license {:name "Unlicense"
            :url  ""}

  :dependencies [[org.clojure/clojure "1.7.0-RC2"]
                 [cljs-http "0.1.43"]]

  :profiles {:dev {:dependencies [[org.clojure/clojurescript "0.0-3308"]
                                  [speclj "3.3.1"]]}}
  :plugins [[speclj "3.3.1"]
            [lein-cljsbuild "1.0.5"]]

  :cljsbuild {:builds        {:dev  {:source-paths   ["src/cljs" "spec/cljs"]
                                     :compiler       {:output-to     "js/lcmap-mastodon_dev.js"
                                                      :optimizations :whitespace
                                                      :pretty-print  true}
                                     :notify-command ["phantomjs"  "bin/speclj" "js/lcmap-mastodon_dev.js"]}

                              :prod {:source-paths ["src/cljs"]
                                     :compiler     {:output-to     "js/lcmap-mastodon.js"
                                                    :optimizations :simple}}}
              :test-commands {"test" ["phantomjs" "bin/speclj" "js/lcmap-mastodon_dev.js"]}}

  :source-paths ["src/cljs"]
  :test-paths ["spec/cljs"]

  :aliases {"cljs" ["do" "clean," "cljsbuild" "once" "dev"]})
