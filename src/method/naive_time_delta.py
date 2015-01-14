__author__ = 'bingo4508'
from src.util import *
import networkx as nx
import json
from collections import Counter
import os
import random as rd


def __increment_dict(d, e, delta=1):
    if e in d:
        d[e] += delta
    else:
        d[e] = delta


def load_model(edge_fn):
    pass


def predict(adopters, m_dir):
    x = {}
    for a in adopters:
        with open('%s/%s.txt' % (m_dir, a), 'r') as f:
            for l in f:
                l = l.strip().split()
                __increment_dict(x, int(l[0]), float(l[1]))
    delete = []
    for k in x:
        if k in adopters:
            delete.append(k)
    for e in delete:
        del x[e]

    candidates = sorted(x.iteritems(), key=lambda (k, v): v, reverse=True)
    return [k for k, v in candidates[:100]]


def train(input_file='../../data/training.txt'):
    threshold = 0.3

    t = load_idea(input_file)
    ideas = list(set(t['idea']))

    for m in range(len(ideas)):
        print m

        n = get_node_by_idea2(t, ideas[m])
        index = sorted(range(len(n['date'])), key=lambda k: n['date'][k])
        node_t = [n['date'][i] for i in index]
        node = [n['node'][i] for i in index]

        for i in range(len(node)-1):     # infect others
            with open('../../output/naive_time_delta/%d.txt' % node[i], 'a') as f:
                for j in range(i+1, len(node)):  # infected
                    interval = (node_t[j]-node_t[i]).days
                    if interval == 0:   # Simultaneously, see as no infection
                        continue
                    if 1.0/interval > threshold:
                        value = 1.0/interval
                        f.write('%d %f\n' % (node[j], value))
                    else:
                        break


if __name__ == '__main__':
    train()