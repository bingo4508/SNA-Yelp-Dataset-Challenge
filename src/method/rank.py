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


def load_model():
    pass


def predict(adopters, model):
    with open(model, 'r') as f:
        x = f.read().strip().split()
        x = [e for e in x if e not in adopters]
    return x[:100]


def train(input_file='../../data/training.txt'):
    t = load_idea(input_file)
    ideas = list(set(t['idea']))

    x = {}
    for m in range(len(ideas)):
        print m
        n = get_node_by_idea2(t, ideas[m])
        for e in n['node']:
          __increment_dict(x, e)

    # Sort
    x = sorted(x.iteritems(), key=lambda (k, v): v, reverse=True)
    with open('../../output/rank.model', 'w') as f:
        for k, v in x:
            f.write('%s ' % k)




if __name__ == '__main__':
    train()