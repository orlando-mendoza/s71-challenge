(defproject s71-challenge "0.1.0-SNAPSHOT"
  :description "S7 Challenge - FIFO multi-queue backed by MySQL"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [mysql/mysql-connector-java "8.0.25"]
                 [seancorfield/next.jdbc "1.2.659"]
                 [integrant "0.8.0"]
                 [environ "1.2.0"]
                 [ring "1.9.3"]
                 [camel-snake-kebab "0.4.2"]
                 [clj-http "3.12.2"]
                 [metosin/reitit "0.5.13"]
                 [ring-cors "0.1.13"]
                 [integrant/repl "0.3.2"]]
  :main ^:skip-aot s71-challenge.server
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev [:project/dev :profiles/dev]
             :profiles/dev {}
             :project/dev {:source-paths   ["dev/src"]
                           :dependencies [[integrant/repl "0.3.1"]
                                          [ring/ring-mock "0.4.0"]]
                           :plugins [[lein-environ "1.2.0"]]}}
  :uberjar-name "mysql_queue.jar")
