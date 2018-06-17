(ns draft.core
  (:require [clj-sparql.core :as q]
            [clojure.string :as str])
  (:gen-class))

(defn req [x]
  (format
   "
  PREFIX owl: <http://www.w3.org/2002/07/owl#>
  PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
  PREFIX foaf: <http://xmlns.com/foaf/0.1/>
  PREFIX dc: <http://purl.org/dc/elements/1.1/>
  PREFIX : <http://dbpedia.org/resource/>
  PREFIX dbpedia2: <http://dbpedia.org/property/>
  PREFIX dbpedia: <http://dbpedia.org/>
  PREFIX skos: <http://www.w3.org/2004/02/skos/core#>

  SELECT ?property ?hasValue
  WHERE {
  { <%s> ?property ?hasValue }
  UNION
  { ?isValueOf ?property <%s> }
  }
  " x x))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def stop-words
  [
   (fn [x] (not (string? (:hasValue x))))
   (fn [x] (str/starts-with? (:property x) "http://www.w3.org"))
   (fn [x] (not (str/starts-with? (:hasValue x) "http")))
   (fn [x] (str/ends-with? (:property x) "label"))
   (fn [x] (str/ends-with? (:property x) "comment"))
   (fn [x] (str/ends-with? (:property x) "sameAs"))
   (fn [x] (str/ends-with? (:property x) "logo"))
   (fn [x] (str/ends-with? (:property x) "image"))
   (fn [x] (str/ends-with? (:property x) "property/symbol"))
   (fn [x] (str/ends-with? (:property x) "seeAlso"))
   (fn [x] (str/ends-with? (:property x) "abstract"))
   (fn [x] (str/includes? (:hasValue x) "wiki"))
   (fn [x] (str/includes? (:property x) "wiki"))
   ])

(defn filter-results [result-set]
  (filter
   (fn [r]
     (not-any? #(% r) stop-words))
   result-set))

(def t86 "http://dbpedia.org/resource/Toyota_86")
(def imp "http://dbpedia.org/resource/Subaru_Impreza")

(defn walk [root lvl]
  (cond
    (= lvl 0) nil
    :default
    (let [st
          (->> (q/query
                {:endpoint "http://dbpedia.org/sparql"}
                (req root))
               filter-results)]
      (mapv #(assoc % :walk (walk (:hasValue %) (dec lvl))) st))))

(def t86-results (walk t86 2))
(def imp-results (walk imp 2))

(spit "t86-results" t86-results)
(spit "imp-results" imp-results)