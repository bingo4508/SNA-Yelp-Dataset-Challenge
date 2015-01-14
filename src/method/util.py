__author__ = 'bingo4508'

import networkx as nx
import datetime
import matplotlib.pyplot as plt


################## Load & Dump #####################

def load_graph(graph_fn):
    g = nx.Graph()
    with open(graph_fn, 'r') as f:
        for line in f:
            line = [int(e) for e in line.strip().split()]
            for e in line[1:]:
                g.add_edge(line[0], e)
    return g


def load_graph_with_idea(graph_fn, idea_fn):
    g = nx.Graph()
    with open(graph_fn, 'r') as f:
        for line in f:
            line = [int(e) for e in line.strip().split()]
            for e in line[1:]:
                g.add_edge(line[0], e)

    with open(idea_fn, 'r') as f:
        for line in f:
            line = line.strip().split()
            d = {}
            node = int(line[0])
            d['idea'] = int(line[1])
            d['date'] = datetime.datetime.strptime(line[2], '%Y/%m/%d').date()
            d['level'] = float(line[3])
            for k, v in d.items():
                g.node[node][k] = v
    return g


def load_idea(idea_fn):
    d = {'node': [], 'idea': [], 'date': [], 'level': []}
    with open(idea_fn, 'r') as f:
        for line in f:
            line = line.strip().split()
            d['node'].append(int(line[0]))
            d['idea'].append(int(line[1]))
            d['date'].append(datetime.datetime.strptime(line[2], '%Y/%m/%d').date())
            d['level'].append(float(line[3]))
    return d


def load_seeds(g, fn):
    # Set seed nodes' state to newly active
    with open(f_seed, 'r') as f:
        l = f.readline().rstrip().split(' ')
        for e in l:
            G.node[int(e)]['state'] = 'n'
            newly_active_nodes.append(int(e))
            seeds.append(int(e))


def load_adopters(adopter_fn):
    with open(adopter_fn, 'r') as f:
        li = [[int(e) for e in l.strip().split()] for l in f]
    return li


def dump_adopters(adopter_fn, adopters):
    with open(adopter_fn, 'w') as f:
        for l in adopters:
            f.write('%s\n' % ' '.join(str(e) for e in l))


################## Evaluate #####################
def evaluate(predict_fn, answer_fn, output_fn=None):
    with open(predict_fn, 'r') as f:
        predict = [[int(e) for e in l.strip().split()] for l in f]

    with open(answer_fn, 'r') as f:
        answer = [[int(e) for e in l.strip().split()] for l in f]
        for i, e in enumerate(answer):
            if len(e) > 100:
                # Because we can guess at most 100 nodes, it's unfair, so answer at most 100
                answer[i] = e[:100]

    f_score = []

    for p, a in zip(predict, answer):
        tp = set(p) & set(a)
        fp = set(p) - tp
        fn = set(a) - set(p)
        precision = float(len(tp)) / (len(tp)+len(fp))
        recall = float(len(tp)) / (len(tp)+len(fn))
        s = (2*precision*recall)/(precision+recall) if (precision+recall) > 0 else 0
        f_score.append(s)
        # f_score.append(precision)

    print "------------- F-Score -------------"
    for i, e in enumerate(f_score):
        print '%d. %f' % (i, e)
    print 'Average: %f' % (sum(f_score)/len(f_score))

    if output_fn is not None:
        try:
            with open(output_fn, 'w') as f:
                f.write("------------- Fscore -------------\n")
                for i, e in enumerate(f_score):
                    f.write('%d. %f\n' % (i, e))
                f.write('Average: %f\n' % (sum(f_score)/len(f_score)))
        except:
            pass


################## Draw #####################

def draw_graph(g, adopters):
    gg = nx.Graph()
    for a in adopters:
        for k in g.neighbors(a):
            gg.add_edge(a, k)
    pos = nx.spring_layout(gg)
    nx.draw_networkx_nodes(gg, pos, nodelist=adopters, node_color='red', node_size=50)
    nx.draw_networkx_nodes(gg, pos, nodelist=list(set(gg.nodes())-set(adopters)), node_color='black', node_size=10)
    nx.draw_networkx_edges(gg, pos, gg.edges())
    plt.show()


##############################################
def get_node_by_idea(train, idea_id):
    index = [i for i, v in enumerate(train['idea']) if v == idea_id]
    return [(train['node'][i], train['date'][i], train['level'][i]) for i in index]


def get_node_by_level(train, level):
    index = [i for i, v in enumerate(train['level']) if v == level]
    return [(train['node'][i], train['idea'][i], train['date'][i]) for i in index]