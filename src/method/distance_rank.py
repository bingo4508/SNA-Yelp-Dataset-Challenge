__author__ = 'bingo4508'
from src.util import *
import networkx as nx
import json
from collections import Counter
import os
import random as rd
from src.one_time_util import *


b = load_json('../../data/Business.txt')
b = {b['business_id'][i]: (b['latitude'][i], b['longitude'][i]) for i in range(len(b['business_id']))}

test_business = '../../data/testing_business.txt'
with open(test_business, 'r') as f:
    tb = f.read().strip().split()

d = {}


def __increment_dict(d, e, delta=1):
    if e in d:
        d[e] += delta
    else:
        d[e] = delta


def load_model():
    pass


def predict(adopters, model):
    if len(d) == 0:
        with open(model, 'r') as f:
            for line in f:
                line = line.strip().split()
                d[line[0]] = (float(line[0], float(line[1])))




    x = [e for e in x if e not in adopters]
    return x[:100]


def train():
    pass


if __name__ == '__main__':
    train()