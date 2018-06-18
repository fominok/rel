import networkx as nx
import matplotlib.pyplot as plt
import json

import urllib.request

print("Input 2 objects and depth to find rels ...")
a = input("First object: ")
b = input("Second object: ")
d = input("Depth: ")

_str = 'http://localhost:8080/?a=http://dbpedia.org/resource/{0}&b=http://dbpedia.org/resource/{1}&d={2}'.format(a, b, d)
with urllib.request.urlopen(_str) as response:
    resp = response.read().decode('utf-8')
    r = json.loads(resp)
    _dir = r['dir']

G=nx.Graph()

for x in _dir:
    for y in x:
        if len(y['f'].split('/')) > 1:
            f = y['f'].split('/')[-1]
        else:
            f = a
        G.add_node(f)

        if len(y['t'].split('/')) > 1:
            t = y['t'].split('/')[-1]
        else:
            t = b
        G.add_node(t)

        if len(y['l'].split('/')) > 1:
            l = y['l'].split('/')[-1]
        else:
            l = y['l']
        G.add_edge(f, t, l=l)


pos = nx.circular_layout(G)

nx.draw(G, with_labels=True, pos=pos, node_size=700)
edge_labels = nx.get_edge_attributes(G,'l')
nx.draw_networkx_edge_labels(G, pos=pos, edge_labels = edge_labels)

manager = plt.get_current_fig_manager()
manager.resize(*manager.window.maxsize())
plt.show()
