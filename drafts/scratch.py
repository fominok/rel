from SPARQLWrapper import SPARQLWrapper, JSON

query = """
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

SELECT ?property ?hasValue ?isValueOf
WHERE {
  { <http://dbpedia.org/resource/Toyota> ?property ?hasValue }
  UNION
  { ?isValueOf ?property <http://dbpedia.org/resource/Toyota> }
}
"""
with open('kek.txt', 'w') as f:
    _not_tup_prop = ('sameAs', 'rdf-schema#label', 'rdf-schema#comment', 'logo', 'image', 'property/symbol', '#seeAlso'\
                , 'wikiPageID', 'wikiPageRevisionID', 'thumbnail', 'http://xmlns.com/', '/property/name', \
                '/property/secCik', '/ontology/abstract', 'prov#wasDerivedFrom', 'property/nativeName', \
                     'property/titlestyle', 'property/width')
    _not_tup_hasV = ('owl#Thing',)
    sparql = SPARQLWrapper("http://dbpedia.org/sparql")
    sparql.setQuery(query)
    sparql.setReturnFormat(JSON)
    results = sparql.query().convert()
    for r in results['results']['bindings']:
        if not r.get('isValueOf') and r['hasValue']['type'] == 'uri':
            if not any(t in r['property']['value'] for t in _not_tup_prop) \
                    and not any(t in r['hasValue']['value'] for t in _not_tup_hasV):
                f.write('PROP: ' + str(r['property']) + ' || ' + 'VALUE: ' + str(r['hasValue']) + '\n')
