__author__ = 'bingo4508'
from src.util import *
from src.model.GT import *
from src.model.IC import *


def __increment_dict(d, e, delta=1):
    if e in d:
        d[e] += delta
    else:
        d[e] = delta


def __update_tao(v, u, tao_vu, delta, Av2u):
    if v not in tao_vu:
        tao_vu[v] = {}
    __increment_dict(tao_vu[v], u, float(delta.days)/Av2u[v][u])


def __update_credit_tao(S, v, u, tao_vu, credit_tao):
    if v[0] not in credit_tao:
        credit_tao[v[0]] = {}
    credit_tao[v[0]][u[0]] = 1.0/sum([1 for e in S if 0 < (u[1]-e[1]).days < tao_vu[v[0]][u[0]]])


def __update_credit(S, v, u, credit):
    if v[0] not in credit:
        credit[v[0]] = {}
    credit[v[0]][u[0]] = 1.0/sum([1 for e in S if e[1] < u[1]])


def load_model(edge_fn, model='IC', threshold_fn=None):
    if model == 'IC':
        return IC(edge_fn)
    else:
        return GT(edge_fn, threshold_fn)


def predict(adopters, model):
    model.set_seeds(adopters)
    result = model.diffuse()
    model.reset()
    # print len(adopters), len(result)
    return result[:100]


def train(input_file='../../data/training3.txt', output_file='../../output/DT'):
    print "Loading data..."
    t = load_idea(input_file)
    g = load_graph('../../data/graph.txt')

    ideas = list(set(t['idea']))
    num_ideas = len(ideas)
    Au = {}
    Av2u = {}
    tao_vu = {}
    credit_vu = []
    credit_tao_vu = []
    infl_u = {}
    pvu = {}

    # Phase 1
    for m in range(len(ideas)):
        credit = {}
        current_table = []
        n = get_node_by_idea(t, ideas[m])
        n.sort(key=lambda x: x[1])
        print "Phase 1 - %d/%d    %d nodes" % (m, num_ideas, len(n))
        for u in n:
            __increment_dict(Au, u[0])
            parents = []
            Av2u[u[0]] = {}
            for v in current_table:
                if g.has_edge(u[0], v[0]) and u[1] > v[1]:
                    __increment_dict(Av2u[v[0]], u[0])
                    __update_tao(v[0], u[0], tao_vu, u[1]-v[1], Av2u)
                    parents.append(v)
                # increment Av&u
            for v in parents:
                __update_credit(parents, v, u, credit)
            current_table.append(u)
        credit_vu.append(credit)

    # Phase 2
    Av2u = {}
    for m in range(len(ideas)):
        credit_tao = {}
        current_table = []
        n = get_node_by_idea(t, ideas[m])
        n.sort(key=lambda x: x[1])
        print "Phase 2 - %d/%d    %d nodes" % (m, num_ideas, len(n))
        for u in n:
            parents = []
            Av2u[u[0]] = {}
            for v in current_table:
                if g.has_edge(u[0], v[0]) and 0 < (u[1] - v[1]).days < tao_vu[v[0]][u[0]]:
                    __increment_dict(Av2u[v[0]], u[0])
                    parents.append(v)
            for v in parents:
                __update_credit_tao(parents, v, u, tao_vu, credit_tao)
            if len(parents) > 0:
                # update infl(u)
                __increment_dict(infl_u, u[0], 1.0/Au[u[0]])
            current_table.append(u)
        credit_tao_vu.append(credit_tao)

    # Calculate pvu
    for c in credit_tao_vu:
        for k, v in c.items():
            if k not in pvu:
                pvu[k] = {}
            for u, credit in v.items():
                __increment_dict(pvu[k], u, float(credit)/Au[k])

    # Output model
    with open(output_file+'.prob', 'w') as f:
        for k in pvu:
            for kk, v in pvu[k].items():
                f.write("%s %s %f\n" % (k, kk, v))
    with open(output_file+'.threshold', 'w') as f:
        threshold = {k: 1.0/v for k, v in infl_u.items()}
        # Normalize to 0~1
        # s = max([v for k, v in threshold.items()])
        # threshold = {k: e/s for k, e in threshold.items()}
        for k, v in threshold.items():
            f.write("%s %f\n" % (k, v))


if __name__=='__main__':
    train()