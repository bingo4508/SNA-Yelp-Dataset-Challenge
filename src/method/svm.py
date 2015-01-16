__author__ = 'bingo4508'

from src.util import *
from src.one_time_util import *
import numpy as np
import networkx as nx
from sklearn.datasets import load_svmlight_file, dump_svmlight_file
from sklearn.svm import OneClassSVM
from sklearn.externals import joblib


def __distance(pair_1, pair_2):
    return ((pair_1[0]-pair_2[0])**2+(pair_1[1]-pair_2[1])**2)**0.5


def train(train='../../output/svm_training.scale', test='../../output/svm_testing_q1_0.scale'):
    X_tr, y_tr = load_svmlight_file(train)
    # X_te, y_te = load_svmlight_file(test)
    clf = OneClassSVM(nu=0.1, kernel="rbf", gamma=0.1)
    clf.fit(X_tr)
    joblib.dump(clf, '../../output/svm_model/model.pkl')


def test(model='../../output/svm_model/model.pkl'):
    clf = joblib.load(model)
    for i in range(100):
        X_te, y_te = load_svmlight_file('../../output/svm_testing_q1_%d.scale' % i)
        pd = clf.predict(X_te)
        c = 0
        for m, n in zip(y_te, pd):
            if m == n:
                c += 1
        print "%d. Correct:%d, Predict:%d, Answer:%d" % (i, c, list(pd).count(1), list(y_te).count(1))


def train_feature(input_file='../../data/training.txt(split)', output='../../output/svm_training.txt'):
    percentage_of_initial_adopter = 0.1

    g = load_graph('../../data/graph.txt')
    b = load_json('../../data/Business.txt')
    b = {b['business_id'][i]: ((b['latitude'][i], b['longitude'][i]), b['stars'][i]) for i in range(len(b['business_id']))}
    u_location = load_user_location('../../data/user_location.txt')

    t = load_idea(input_file)
    ideas = list(set(t['idea']))

    l = None
    for m in range(len(ideas)):
        print m
        n = get_node_by_idea2(t, ideas[m])
        index = sorted(range(len(n['date'])), key=lambda k: n['date'][k])
        # level = [n['level'][i] for i in index]
        node = [n['node'][i] for i in index]
        initial_adopters = node[: int(percentage_of_initial_adopter*len(node))]
        laters = node[int(percentage_of_initial_adopter*len(node)):]
        # b_avg_stars = np.average([level[: 0.1*len(level)]])

        for e in laters:
            features = []
            # Distance with business
            features.append(__distance(b[ideas[m]][0], u_location[e]))

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

            features = np.array([features])
            l = features if l is None else np.concatenate((l, features))

    dump_svmlight_file(l, [1]*l.shape[0], output)


def test_feature(input='../../data/test_data/test_data_q1.txt', output='../../output/svm_testing_q1_'):
    g = load_graph('../../data/graph.txt')
    b = load_json('../../data/Business.txt')
    b = {b['business_id'][i]: ((b['latitude'][i], b['longitude'][i]), b['stars'][i]) for i in range(len(b['business_id']))}
    u_location = load_user_location('../../data/user_location.txt')
    t = load_idea('../../data/testing.txt')

    with open('../../data/testing_business.txt', 'r') as f:
        tb = f.read().strip().split()
    with open(input, 'r') as f:
        test = [[e for e in l.strip().split()] for l in f]


    for i, (business, initial_adopters) in enumerate(zip(tb, test)):
        print i
        l = None
        answers = []

        n = get_node_by_idea2(t, business)
        ans = set(n['node'])-set(initial_adopters)
        candidates = set(g.nodes())-set(initial_adopters)
        for e in candidates:
            features = []
            # Distance with business
            features.append(__distance(b[business][0], u_location[e]))

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

            features = np.array([features])
            l = features if l is None else np.concatenate((l, features))
            answers.append(1 if e in ans else 0)

        dump_svmlight_file(l, answers, output+str(i)+'.txt')



if __name__ == '__main__':
    test()