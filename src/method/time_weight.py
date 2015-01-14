__author__ = 'bingo4508'
# This model takes the time delta as the only degree of influence of nodes
# Taking same idea, an earlier adopter n1 at t1 will influence later adopters, say n2 at t2 with influence 1/(t2-t1)
# if the influence is over a threshold (default=0.01)


# Q: Is those who has higher adoption level are more likely to spread?

from src.util import *
import networkx as nx
import json
from collections import Counter
import os
import random as rd


def __remove_keys(d, keys):
    for k in d:
        if k in keys:
            del d[k]


def load_model(adopters, m_dir, g):
    # a = []
    # for e in adopters:
    #     a += e
    a = adopters

    model = Counter()

    # 1st infection
    for j in set(a):
        with open('%s/%d.txt' % (m_dir, j), 'r') as f:
            model += Counter(json.loads(f.read()))

    # Remove adopters + Sort candidates by influence
    __remove_keys(model, adopters)
    candidates = sorted(model.iteritems(), key=lambda (k, v): v, reverse=True)

    # 2nd infection
    # second_set = [int(k) for k, v in candidates[:100]]
    # for j in second_set:
    #     with open('%s/%d.txt' % (m_dir, j), 'r') as f:
    #         model += Counter(json.loads(f.read()))
    #
    # # Remove adopters + Sort candidates by influence
    # __remove_keys(model, adopters)
    # candidates = sorted(model.iteritems(), key=lambda (k, v): v, reverse=True)

    print candidates[:100]

    return candidates


def predict(g, adopters, model):
    # Predict
    candidates = model
    # Pick top 100
    candidates = [k for k, v in candidates[:100]]
    return candidates


def train(input_file='../../data/training.txt', multiply_idea_degree=False):
    print "Loading data..."
    threshold = 0.1
    t = load_idea(input_file)

    ideas = list(set(t['idea']))
    num_ideas = len(ideas)

    for m in range(len(ideas)):
        n = get_node_by_idea(t, ideas[m])
        n.sort(key=lambda x: x[1])

        print "%d/%d    %d nodes" % (m, num_ideas, len(n))
        # Calculate influence to others
        infect_degree = {}
        for i in range(len(n)-1):     # infect others
            infect_degree[n[i][0]] = {}
            for j in range(i+1, len(n)):  # infected
                interval = (n[j][1]-n[i][1]).days
                if interval == 0:   # Simultaneously, see as no infection
                    continue
                if 1.0/interval > threshold:
                    value = round((1.0/interval)*(n[j][2] if multiply_idea_degree else 1), 5)   # n[j][2]: degree of adoption

                    if n[j][0] in infect_degree[n[i][0]]:
                        infect_degree[n[i][0]][n[j][0]] += value
                    else:
                        infect_degree[n[i][0]][n[j][0]] = value
                else:
                    break

        with open('../../output/TW_tmp(threshold=0.1)/tmp_%d' % m, 'w') as f:
            f.write(json.dumps(infect_degree))
    __merge()


def __merge():
    for i in range(1000):
        print i
        with open('../../output/TW_tmp(threshold=0.1)/tmp_%d' % i, 'r') as f:
            x = json.loads(f.read())
            for k in x:
                fn = '../../output/TW_node_influence(threshold=0.1)/%s.txt' % k
                if os.path.isfile(fn):
                    y = Counter(x[k])+Counter(json.loads(open(fn, 'r').read()))
                    with open(fn, 'w') as ff:
                        ff.write(json.dumps(y))
                else:
                    with open(fn, 'w') as ff:
                        ff.write(json.dumps(x[k]))


# Multiply final time weight with adar score
# def plus_adar():
#     g = load_graph('../../data/graph.txt')
#     for n in g.nodes():
#         fn = '../../output/TW_node_influence/%d.txt' % n
#         fn_o = '../../output/TW_node_influence(+adar)/%d.txt' % n
#         if os.path.isfile(fn):
#             with open(fn, 'r') as f:
#                 x = json.loads(open(fn, 'r').read())
#                 for k in x:
#                     x[k] += [e for e in nx.adamic_adar_index(g, [(n, int(k))])][0][2]
#                 with open(fn_o, 'w') as ff:
#                     ff.write(json.dumps(x))


def __research():
    t = load_idea('../../data/training.txt')
    g = load_graph('../data/graph.txt')
    # Test how many nodes have been affected by others according to date
    ideas = list(set(t['idea']))
    for m in range(5):
        n = get_node_by_idea(t, ideas[m])
        n.sort(key=lambda x: x[1], reverse=True)
        c = 0
        for i in range(len(n)):
            for j in range(i, len(n)):
                if g.has_edge(n[i][0], n[j][0]):
                    c += 1
                    break
        print len(n), c


if __name__ == '__main__':
    train()

