__author__ = 'bingo4508'
from src.util import *
from src.one_time_util import *
import numpy as np
import networkx as nx
from sklearn.datasets import dump_svmlight_file



def __distance(pair_1, pair_2):
    return ((pair_1[0]-pair_2[0])**2+(pair_1[1]-pair_2[1])**2)**0.5


def __load(fn):
    # ex: {business: (user, label)...}
    d = {}
    with open(fn, 'r') as f:
        for l in f:
            l = l.strip().split()
            append_dict(d, l[0], (l[1], l[3]))
    return d


def train_feature(fn_list, input_file='../../output/svm_feature_merge/training.txt(split)'):

    percentage_of_initial_adopter = 0.1

    g = load_graph('../../data/graph.txt')
    b = load_json('../../data/Business.txt')
    b = {b['business_id'][i]: ((b['latitude'][i], b['longitude'][i]), b['stars'][i]) for i in range(len(b['business_id']))}
    u_location = load_user_location('../../data/user_location.txt')
    t = load_idea(input_file)

    for fn in fn_list:
        fn = '../../output/svm_feature_merge/' + fn
        d = __load(fn)
        with open(fn+'(f0).txt', 'w') as f0:
            with open(fn+'(f1).txt', 'w') as f1:
                with open(fn+'(f2).txt', 'w') as f2:
                    with open(fn+'(f3).txt', 'w') as f3:
                        for k, v in d.items():
                            print k
                            user = [q for q, qq in v]
                            label = [qq for q, qq in v]

                            n = get_node_by_idea2(t, k)
                            print len(n['date'])
                            index = sorted(range(len(n['date'])), key=lambda k: n['date'][k])
                            # level = [n['level'][i] for i in index]
                            node = [n['node'][i] for i in index]
                            initial_adopters = node[: int(percentage_of_initial_adopter*len(node))]

                            assert(len(set(user) & set(initial_adopters)) == 0)
                            # b_avg_stars = np.average([level[: 0.1*len(level)]])

                            for e, lb in zip(user,label):
                                features = []
                                # Distance with business
                                features.append(__distance(b[k][0], u_location[e]))

                                # Difference between user average stars and business average stars by adopters

                                # Average distance with initial adopters
                                features.append(np.average([__distance(u_location[a], u_location[e]) for a in initial_adopters]))

                                # Percentage of friends in initial adopters
                                features.append(float(sum([1 if g.has_edge(e, a) else 0 for a in initial_adopters]))/len(initial_adopters))

                                # Average adar
                                preds = nx.adamic_adar_index(g, [(e, a) for a in initial_adopters if g.has_node(a) and g.has_node(e)])
                                try:
                                    preds = [p for u, v, p in preds]
                                    preds = preds if preds else 0
                                except Exception:
                                    preds = 0
                                features.append(np.average(preds))

                                f0.write('{0} {1} {2} {3}\n'.format(k, e, features[0], lb))
                                f1.write('{0} {1} {2} {3}\n'.format(k, e, features[1], lb))
                                f2.write('{0} {1} {2} {3}\n'.format(k, e, features[2], lb))
                                f3.write('{0} {1} {2} {3}\n'.format(k, e, features[3], lb))


if __name__ == '__main__':
    fn_list = ['BipartieCommonNeighbor_testing1.txt', 'BipartieCommonNeighbor_training1.txt', 'ICSpreadSample_testing1.txt', 'ICSpreadSample_training1.txt']
    train_feature(fn_list)
