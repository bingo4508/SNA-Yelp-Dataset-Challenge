__author__ = 'bingo4508'

from src.util import *
import networkx as nx
from collections import Counter
from src.model.IC import *


def load_model(edge_fn):
    return IC(edge_fn)


def predict(adopters, model):
    model.set_seeds(adopters)
    result = model.diffuse()
    model.reset()
    print len(adopters), len(result)
    return result[:100]


def train(input_file='../../data/training.txt', output_file='../../output/bernouli.model'):
    print "Loading data..."
    t = load_idea(input_file)
    g = load_graph('../../data/graph.txt')

    ideas = list(set(t['idea']))
    num_ideas = len(ideas)
    infect_count = {}
    just_count = {}

    for m in range(len(ideas)):
        n = get_node_by_idea(t, ideas[m])
        n.sort(key=lambda x: x[1])
        print "%d/%d    %d nodes" % (m, num_ideas, len(n))
        tmp_infect_count = {}
        for e in n:
            e = e[0]
            if e in just_count:
                just_count[e] += 1
            else:
                just_count[e] = 1
            tmp_infect_count[e] = {}
            for ee in g.neighbors(e):
                if ee in tmp_infect_count:
                    if ee in tmp_infect_count[e]:
                        tmp_infect_count[e][ee] += 1
                    else:
                        tmp_infect_count[e][ee] = 1
        for k, v in tmp_infect_count.items():
            if k in infect_count:
                infect_count[k] += Counter(v)
            else:
                infect_count[k] = Counter(v)

    # Output result
    with open(output_file, 'w') as f:
        for k in infect_count:
            Av = just_count[k]
            for kk, v in infect_count[k].items():
                Av2u = float(v)/Av
                f.write("%d %d %f\n" % (k, kk, Av2u))


if __name__ == '__main__':
    train()