from SPARQLWrapper import SPARQLWrapper, JSON
import json

query = """
SELECT ?property ?hasValue ?isValueOf
WHERE {
  { <http://dbpedia.org/resource/Toyota> ?property ?hasValue }
  UNION
  { ?isValueOf ?property <http://dbpedia.org/resource/Toyota> }
}
"""
sparql = SPARQLWrapper("http://dbpedia.org/sparql")
sparql.setQuery(query)
sparql.setReturnFormat(JSON)
results = sparql.query().convert()
print(results['results']['bindings'])
#parsed = json.loads(str(results))
#print(json.dumps(parsed, indent=4, sort_keys=True))