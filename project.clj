(defproject cheffy "0.1.0-SNAPSHOT"
  :description "Cheffy REST API"
  :url "https://github.com/suthsc/cheffy"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring "1.8.1"]
                 [integrant "0.8.0"]
                 [environ "1.2.0"]
                 [metosin/reitit "0.5.18"]
                 [seancorfield/next.jdbc "1.1.646"]
                 [org.postgresql/postgresql "42.6.0"]
                 [clj-http "3.12.3"]
                 [ovotech/ring-jwt "1.3.0"]]
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev {:source-paths ["dev/src"]
                   :resource-paths ["dev/resources"]
                   :dependencies [[ring/ring-mock "0.4.0"]
                                  [integrant/repl "0.3.2"]]}}
  :uberjar-name "cheffy.jar")
