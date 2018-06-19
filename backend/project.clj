(defproject draft "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-json "0.4.0"]
                 [ring "1.7.0-RC1"]
                 [http-kit "2.2.0"]
                 [org.apache.jena/jena-arq "2.12.0"]]
  :main ^:skip-aot draft.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
