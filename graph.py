import networkx as nx
import matplotlib.pyplot as plt
import random

_dir = [
        [{"f": "x", "t": "y", "l": "label",}, {"f": "y", "t": "z", "l": "label2",}, {"f": "z", "t": "a", "l": "k",},],
        [{"f": "x", "t": "b", "l": "kek",}, {"f": "b", "t": "a", "l": "label4",},],
        [{"f": "x", "t": "f", "l": "1337",}, {"f": "f", "t": "a", "l": "420",},],
]
G=nx.Graph()

for x in _dir:
    for y in x:
        G.add_node(y['f'])
        G.add_node(y['t'])
        G.add_edge(y['f'], y['t'], l=y['l'])



nx.draw(G, with_labels=True)
edge_labels = nx.get_edge_attributes(G,'l')
nx.draw_networkx_edge_labels(G, pos=nx.spring_layout(G), edge_labels = edge_labels, label_pos=0.3)

manager = plt.get_current_fig_manager()
manager.resize(*manager.window.maxsize())
plt.show()
