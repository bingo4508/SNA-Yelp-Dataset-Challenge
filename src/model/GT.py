__author__ = 'bingo4508'
import networkx as nx
import random as rd


# General Threshold Model
class GT:
    def __init__(self, edge_fn, threshold_fn):
        self.g = nx.DiGraph()
        self.load_graph(edge_fn, threshold_fn)

    def set_seeds(self, seeds):
        self.newly_active_nodes = [e for e in seeds if e in self.g.nodes()]
        self.seeds = [e for e in seeds if e in self.g.nodes()]

        # Set seed nodes' state to newly active
        for e in self.seeds:
            self.g.node[e]['state'] = 'n'

    def load_graph(self, edge_fn, threshold_fn):
        with open(edge_fn, 'r') as f:
            for l in f:
                l = l.strip().split()
                self.g.add_node(l[0], state='i', threshold=0, influenced=0)
                self.g.add_node(l[1], state='i', threshold=0, influenced=0)
                self.g.add_edge(l[0], l[1], influence=float(l[2]))

        # Nodes' threshold
        with open(threshold_fn, 'r') as f:
            for l in f:
                l = l.strip().split()
                self.g.node[l[0]]['threshold'] = float(l[1])/4

    def diffuse(self):
        active_node_list = {}
        while True:
            copy_newly_active_nodes = self.newly_active_nodes[:]
            self.newly_active_nodes = []
            if copy_newly_active_nodes:
                for n in copy_newly_active_nodes:
                    self.g.node[n]['state'] = 'a'
                    for e in self.g.successors(n):
                        if self.g.node[e]['state'] == 'i':
                            pu = self.g.node[e]['influenced']
                            self.g.node[e]['influenced'] = pu + (1-pu) * self.g.edge[n][e]['influence']
                            # self.g.node[e]['influenced'] += self.g.edge[n][e]['influence']

                            if self.g.node[e]['influenced'] >= self.g.node[e]['threshold']:
                                # print "ACTIVATE!"
                                self.g.node[e]['state'] = 'n'
                                self.newly_active_nodes.append(e)
                                active_node_list[e] = self.g.node[e]['influenced']
            else:
                x = [(n, self.g.node[n]['influenced']) for n in self.g.nodes() if self.g.node[n]['influenced'] > 0]

                # y = []
                # for k, v in x:
                #     if k in active_node_list:
                #         y.append((k, v*3))
                #     else:
                #         y.append((k, v))
                # x = y

                candidates = sorted(x, key=lambda (k, v): v, reverse=True)
                return [k for k, v in candidates]

    def reset(self):
        for n in self.g.nodes():
            self.g.node[n]['state'] = 'i'
            self.g.node[n]['influenced'] = 0
