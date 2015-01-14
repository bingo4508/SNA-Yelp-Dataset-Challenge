__author__ = 'bingo4508'

from util import *
from method.baseline import *


g = load_graph('../data/graph.txt')
for i in [1, 2, 3]:
    INITIAL_ADOPTERS = '../data/test_data/test_data_q%d.txt' % i
    PREDICT = '../output/test_data_my_a%d.txt' % i
    ANSWER = '../data/test_data/test_data_a%d.txt' % i
    RESULT = '../output/test_data_my_a%d_result.txt' % i
    strategy = baseline

    adopters = load_adopters(INITIAL_ADOPTERS)
    draw_graph(g, adopters[0])

    # infected = []
    #
    # for e in adopters:
    #     infected.append(strategy(g, e))
    #
    # dump_adopters(PREDICT, infected)
    # evaluate(PREDICT, ANSWER, RESULT)