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

(defn traverse [acc root]
  (if (not-empty root)
    (conj acc (mapv #(traverse (conj acc (:hasValue %)) (:walk %)) root))
    acc))

(defn find* [acc ds v]
  (if-let [found (first (filter #(= (:hasValue %) v) ds))]
    (conj acc (dissoc found :walk))
    (first (filter #(not-empty %)
                   (mapv #(find* (conj acc (dissoc % :walk)) (:walk %) v)
                         (filter #(not-empty (:walk %)) ds))))))

(defn find [ds v]
  (find* [] ds  v))

(defn cutoff [is chain]
  (let [l (map :hasValue (butlast chain))]
    (empty? (clojure.set/intersection (set l) is))))

(defn links [ds1 ds2]
  (let [d1 (flatten (traverse [] ds1))
        d2 (flatten (traverse [] ds2))
        is (clojure.set/intersection (set d1) (set d2))
        d1-chains (filterv (partial cutoff is) (map #(find ds1 %) is))
        d2-chains (filterv (partial cutoff is) (map #(find ds2 %) is))]
    [d1-chains d2-chains]))

(defn join-links [d1-chains d2-chains]
  (mapv
   (fn [c]
     (let [chain2 (first (filter #(= (last %) (last c)) d2-chains))]
       (into c (vec (reverse (butlast chain2))))))
   d1-chains))

(defn build-edges [chain]
  (let [fst (first chain)
        lst (last chain)
        ae {:f "a"
            :t (:hasValue fst)
            :l (:property fst)}
        eb {:f (:hasValue lst)
            :t "b"
            :l (:property lst)}
        others
        (reduce
         (fn [acc i]
           (let [prev (nth chain (dec i))
                 cur (nth chain i)]
             (conj acc {:f (:hasValue prev)
                        :t (:hasValue cur)
                        :l (:property cur)})))
         [] (range 1 (count chain)))]
    (vec (concat [ae] others [eb]))))

(defn build-all [ds1 ds2]
  (mapv
   build-edges
   (let [[a b] (links ds1 ds2)]
     (join-links a b))))

(defn rel [q1 q2 d]
  (let [ds1 (future (walk q1 d))
        ds2 (future (walk q2 d))]
    (build-all @ds1 @ds2)))

(comment
  (def t86 "http://dbpedia.org/resource/Toyota_86")
  (def imp "http://dbpedia.org/resource/Subaru_Impreza")

  (def test1 (rel t86 imp 2))

  (def rofls-royce "http://dbpedia.org/resource/Lada_Kalina")
  (def lmaoborghini "http://dbpedia.org/resource/Lamborghini_Aventador")

  (def le-mans (rel rofls-royce lmaoborghini 2))

  )
