__author__ = 'bingo4508'

import networkx as nx
import datetime
import matplotlib.pyplot as plt



OUTPUT = '../../output/'

################## Load & Dump #####################

def load_graph(graph_fn):
    g = nx.Graph()
    with open(graph_fn, 'r') as f:
        for line in f:
            line = [e for e in line.strip().split()]
            for e in line[1:]:
                g.add_edge(line[0], e)
    return g


def load_graph_with_idea(graph_fn, idea_fn):
    g = nx.Graph()
    with open(graph_fn, 'r') as f:
        for line in f:
            line = [e for e in line.strip().split()]
            for e in line[1:]:
                g.add_edge(line[0], e)

    with open(idea_fn, 'r') as f:
        for line in f:
            line = line.strip().split()
            d = {}
            node = line[0]
            d['idea'] = line[1]
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
            d['node'].append(line[0])
            d['idea'].append(line[1])
            d['date'].append(datetime.datetime.strptime(line[2], '%Y/%m/%d').date())
            d['level'].append(float(line[3]))
    return d


def load_adopters(adopter_fn):
    with open(adopter_fn, 'r') as f:
        li = [[e for e in l.strip().split()] for l in f]
    return li


def dump_adopters(adopter_fn, adopters):
    with open(adopter_fn, 'w') as f:
        for l in adopters:
            f.write('%s\n' % ' '.join(str(e) for e in l))


################## Evaluate #####################
# metric: 'f1', 'precision', 'recall'
def evaluate(predict_fn, answer_fn, output_fn=None, metric='f1'):
    with open(predict_fn, 'r') as f:
        predict = [[e for e in l.strip().split()] for l in f]

    with open(answer_fn, 'r') as f:
        answer = [[e for e in l.strip().split()] for l in f]
        for i, e in enumerate(answer):
            if len(e) > 100:
                answer[i] = e[:100]

    score = []
    x = []
    for p, a in zip(predict, answer):
        try:
            tp = set(p) & set(a)
            fp = set(p) - tp
            fn = set(a) - set(p)
            precision = float(len(tp)) / (len(tp)+len(fp))
            recall = float(len(tp)) / (len(tp)+len(fn))
            s = (2*precision*recall)/(precision+recall)
            if metric == 'f1':
                score.append(s)
            elif metric == 'precision':
                score.append(precision)
            elif metric == 'recall':
                score.append(recall)
        except Exception:
            score.append(0)
        x.append([a.index(e) if e in a else '.' for e in p])

    print "------------- %s -------------" % metric
    for i, e in enumerate(score):
        print '%d. %f' % (i, e), x[i]
    print 'Average: %f' % (sum(score)/len(score))

    if output_fn is not None:
        try:
            with open(output_fn, 'w') as f:
                f.write("------------- Fscore -------------\n")
                for i, e in enumerate(score):
                    f.write('%d. %f\n' % (i, e))
                f.write('Average: %f\n' % (sum(score)/len(score)))
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


def get_node_by_idea2(train, idea_id):
    index = [i for i, v in enumerate(train['idea']) if v == idea_id]
    return {'node': [train['node'][i] for i in index], 'date': [train['date'][i] for i in index], 'level': [train['level'][i] for i in index]}


def get_node_by_level(train, level):
    index = [i for i, v in enumerate(train['level']) if v == level]
    return [(train['node'][i], train['idea'][i], train['date'][i]) for i in index]