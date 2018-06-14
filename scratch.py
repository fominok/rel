from SPARQLWrapper import SPARQLWrapper, JSON

query = """
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
        if not r.get('isValueOf'):
            if not any(t in r['property']['value'] for t in _not_tup_prop) \
                    and not any(t in r['hasValue']['value'] for t in _not_tup_hasV):
                f.write('PROP: ' + str(r['property']) + ' || ' + 'VALUE: ' + str(r['hasValue']) + '\n')
