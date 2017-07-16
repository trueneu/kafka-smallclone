(defproject trueneu/kafka-smallclone "0.1.1"
  :description "An ad-hoc tool for re-producing data from one Kafka topic to another"
  :url "https://github.com/trueneu/kafka-smallclone/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ymilky/franzy "0.0.1"]
                 [ymilky/franzy-common "0.0.1"]
                 [ymilky/franzy-admin "0.0.1"]
                 [org.clojure/tools.cli "0.3.5"]]

  :profiles {:common  {:uberjar {:aot :all}
                       :dev {:plugins [[lein-binplus "0.6.2"]]}
                       :main kafka-smallclone.core
                       :uberjar-name "kafka-smallclone.jar"
                       :bin {:name "kafka-smallclone"}}})

