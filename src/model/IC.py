__author__ = 'bingo4508'
import networkx as nx
import random as r


class IC:
    def __init__(self, edge_fn):
        self.g = nx.DiGraph()
        self.load_graph(edge_fn)

    def set_seeds(self, seeds):
        self.newly_active_nodes = [e for e in seeds if e in self.g.nodes()]
        self.seeds = [e for e in seeds if e in self.g.nodes()]

        # Set seed nodes' state to newly active
        for e in self.seeds:
            if e in self.g.nodes():
                self.g.node[e]['state'] = 'n'

    def load_graph(self, edge_fn):
        with open(edge_fn, 'r') as f:
            for l in f:
                l = l.strip().split()
                self.g.add_node(l[0], state='i')
                self.g.add_node(l[1], state='i')
                self.g.add_edge(l[0], l[1], prob=float(l[2]))

        # for n1, n2 in self.g.edges():
        #     if 'prob' not in self.g.edge[n1][n2]:
        #         self.g.edge[n1][2]['prob'] = 0

    def diffuse(self):
        active_node_list = {}
        active_nodes = []
        while True:
            copy_newly_active_nodes = self.newly_active_nodes[:]
            self.newly_active_nodes = []
            if copy_newly_active_nodes:
                for n in copy_newly_active_nodes:
                    self.g.node[n]['state'] = 'a'
                    active_nodes.append(n)
                    for e in self.g.neighbors(n):
                        if r.random() < self.g.edge[n][e]['prob'] and self.g.node[e]['state'] == 'i':
                            self.g.node[e]['state'] = 'n'
                            self.newly_active_nodes.append(e)
                            if e in active_node_list:
                                active_node_list[e] += self.g.edge[n][e]['prob']
                            else:
                                active_node_list[e] = self.g.edge[n][e]['prob']
            else:
                # candidates = [e for e in active_nodes if e not in self.seeds]
                # return candidates

                candidates = sorted(active_node_list.iteritems(), key=lambda (k, v): v, reverse=True)
                return [k for k, v in candidates]

    def reset(self):
        for n in self.g.nodes():
            self.g.node[n]['state'] = 'i'
