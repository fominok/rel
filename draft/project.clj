(defproject draft "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 #_[clj-sparql "0.2.0"] ;; Inlining this
                 [org.apache.jena/jena-arq "2.12.0"]]
  :main ^:skip-aot draft.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
