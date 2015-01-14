__author__ = 'bingo4508'

from util import *
import json
from collections import Counter
import os

# Predictors
# from method.baseline import predict
# from method.time_weight import predict, load_model
# from method.bernouli import predict, load_model
# from method.discrete_time import predict, load_model
# from method.naive_time_delta import *
# from method.rank import *
# from method.naive import *
from method.distance_rank import *


g = load_graph('../data/graph.txt')
OUTPUT = '../output'
TEST_DATA = '../data/test_data'

for i in [1, 2, 3]:
    print i

    INITIAL_ADOPTERS = os.path.join(TEST_DATA, 'test_data_q%d.txt' % i)
    PREDICT = os.path.join(OUTPUT, 'test_data_my_a%d.txt' % i)
    ANSWER = os.path.join(TEST_DATA, 'test_data_a%d.txt' % i)
    RESULT = os.path.join(OUTPUT, 'test_data_my_a%d_result.txt' % i)


    adopters = load_adopters(INITIAL_ADOPTERS)
    infected = []

    ####################################################################

    # Time weight model
    # model = load_model(adopters, '../output/TW_node_influence(threshold=0.1)', g)

    # Bernouli model
    # model = load_model('../output/DT.prob')

    # Discrete time model
    # model = load_model('../output/DT.prob', 'IC')
    # model = load_model('../output/DT.prob', 'GT', '../output/DT.threshold')


    #####################################################################
    #Predict
    for j, e in enumerate(adopters):
        # Time weight model
        # model = load_model(e, '../output/TW_node_influence(threshold=0.1)', g)
        # r = predict(g, e, model)

        # Bernouli model
        # r = predict(e, model)

        # Discrete time model
        # r = predict(e, model)

        # Rank model
        # r = predict(e, '../output/rank.model')

        # Naive model
        # threshold = 10
        # if len(e) > threshold:
        #     e = e[-threshold:]

        # r = predict(e, '../output/naive3_100')

        # Distance model
        r = predict(j, '../data/user_location.txt')

        infected.append(r)

    dump_adopters(PREDICT, infected)
    evaluate(PREDICT, ANSWER, RESULT, 'f1')
