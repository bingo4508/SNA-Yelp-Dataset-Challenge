__author__ = 'bingo4508'

import random as rd
import datetime
import os

from one_time_util import *
from util import *


def generate_graph():
    users = load_json('../data/User.txt')

    print("GO!")
    with open('../output/graph.txt', 'w') as f:
        for i, u in enumerate(users['user_id']):
            if len(users['friends'][i]) > 0:
                f.write('%s %s\n' % (u, ' '.join(users['friends'][i])))


def generate_testing(r, c, date=None):
    d = {}
    index = sorted(range(len(r['business_id'])), key=lambda k: (r['business_id'][k], r['date'][k]))
    for i in index:
        if r['business_id'][i] in c:
            if date:
                if r['date'][i] < date:
                    continue
            append_dict(d, r['business_id'][i], r['user_id'][i])

    for i in [1, 2, 3]:
        with open('../output/test_data_q%d.txt' % i, 'w') as fq:
            with open('../output/test_data_a%d.txt' % i, 'w') as fa:
                for k in d:
                    cut = int(len(d[k])*i*0.1)
                    fq.write(' '.join(d[k][:cut])+'\n')
                    fa.write(' '.join(d[k][cut:])+'\n')


def generate_training2():
    r = load_json('../data/Review.txt', True, True)
    d = {}
    for i in range(len(r['business_id'])):
        increment_dict(d, r['business_id'][i])

    c = []
    for k in d:
        if d[k] > 100:
            c.append(k)

    # Random select 100 business having more than 100 reviews
    c = rd.sample(c, 100)
    
    index = sorted(range(len(r['business_id'])), key=lambda k: (r['business_id'][k], r['date'][k]))

    print("GO!")
    with open('../output/training.txt', 'w') as f:
        for i in index:
            if r['business_id'][i] not in c:
                f.write("%s %s %s %f\n" % (r['user_id'][i], r['business_id'][i], r['date'][i].strftime("%Y/%m/%d"), r['stars'][i]))

    with open('../output/testing_business.txt', 'w') as f:
        f.write(' '.join(c))

    # Generate 3 testing data
    generate_testing(r, c)


def generate_training3(date=datetime.datetime(2014, 1, 1)):
    r = load_json('../data/Review.txt', True, True)
    d = {}
    for i in range(len(r['business_id'])):
        if r['date'][i] > date:
            increment_dict(d, r['business_id'][i])

    c = []
    for k in d:
        if d[k] > 100:
            c.append(k)

    # Random select 10 business having more than 100 reviews
    c = rd.sample(c, 10)

    index = sorted(range(len(r['business_id'])), key=lambda k: (r['business_id'][k], r['date'][k]))

    print("GO!")
    with open('../output/training.txt', 'w') as f:
        for i in index:
            if r['date'][i] < date:
                f.write("%s %s %s %f\n" % (r['user_id'][i], r['business_id'][i], r['date'][i].strftime("%Y/%m/%d"), r['stars'][i]))

    with open('../output/testing_business.txt', 'w') as f:
        f.write(' '.join(c))

    # Generate 3 testing data
    generate_testing(r, c, date)


def generate_training4():
    r = load_json('../data/Review.txt', True, True)
    d = {}
    for i in range(len(r['business_id'])):
        increment_dict(d, r['business_id'][i])

    c = []
    for k in d:
        if d[k] > 100:
            c.append(k)

    # Random select 10 business having more than 100 reviews
    c_t = rd.sample(c, 100)
    with open('../output/testing_business.txt', 'w') as f:
        f.write(' '.join(c_t))
    c = set(c)-set(c_t)

    index = sorted(range(len(r['business_id'])), key=lambda k: (r['business_id'][k], r['date'][k]))

    print("GO!")
    with open('../output/training.txt', 'w') as f:
        for i in index:
            if r['business_id'][i] in c:
                f.write("%s %s %s %f\n" % (r['user_id'][i], r['business_id'][i], r['date'][i].strftime("%Y/%m/%d"), r['stars'][i]))

    # Generate 3 testing data
    generate_testing(r, c_t)


# midpoint of the geographic coordinate of the businesses of a user
def generate_user_location():
    test_business = '../data/testing_business.txt'
    with open(test_business, 'r') as f:
        tb = f.read().strip().split()

    r = load_json('../data/Review.txt', True, True)
    b = load_json('../data/Business.txt')
    b = {b['business_id'][i]: (b['latitude'][i], b['longitude'][i]) for i in range(len(b['business_id']))}
    d = {}
    for i in range(len(r['business_id'])):
        if b[r['business_id'][i]] not in tb:
            append_dict(d, r['user_id'][i], b[r['business_id'][i]])
    for k, v in d.items():
        # (latitude, longitude)
        s = [0, 0]
        for e in v:
            s[0] += e[0]
            s[1] += e[1]
        s = (s[0]/len(e), s[1]/len(e))
        d[k] = s
    with open('../output/user_location(latitude_longitude).txt', 'w') as f:
        for k, v in d.items():
            f.write('%s %f %f\n' % (k, v[0], v[1]))


def generate_user_location():
    test_business = '../data/testing_business.txt'
    with open(test_business, 'r') as f:
        tb = f.read().strip().split()

    r = load_json('../data/Review.txt', True, True)
    b = load_json('../data/Business.txt')
    b = {b['business_id'][i]: (b['latitude'][i], b['longitude'][i]) for i in range(len(b['business_id']))}
    d = {}
    for i in range(len(r['business_id'])):
        if b[r['business_id'][i]] not in tb:
            append_dict(d, r['user_id'][i], b[r['business_id'][i]])
    for k, v in d.items():
        # (latitude, longitude)
        s = [0, 0]
        for e in v:
            s[0] += e[0]
            s[1] += e[1]
        s = (s[0]/len(e), s[1]/len(e))
        d[k] = s
    with open('../output/user_location(latitude_longitude).txt', 'w') as f:
        for k, v in d.items():
            f.write('%s %f %f\n' % (k, v[0], v[1]))


def split_training_data(fn, num, threshold):
    r = load_json('../data/Review.txt', True, True)
    d = {}
    with open(fn, 'r') as f:
        for l in f:
            increment_dict(d, l.strip().split()[1])

    c = []
    for k in d:
        if d[k] > threshold:
            c.append(k)

    c_split = rd.sample(c, num)
    with open(fn+'(split_businesses)', 'w') as f:
        for e in c_split:
            f.write('%s ' % e)

    with open(fn+'(split)', 'w') as f1:
        with open(fn+'(else)', 'w') as f2:
            with open(fn, 'r') as f3:
                for l in f3:
                    x = l.strip().split()
                    if x[1] in c_split:
                        f1.write(l)
                    else:
                        f2.write(l)
    generate_testing(r, c_split)


def generate_blablabla():
    r = load_json('../data/Review.txt', True, True)
    test_business = '../data/testing_business.txt'
    with open(test_business, 'r') as f:
        tb = f.read().strip().split()

    index = sorted(range(len(r['business_id'])), key=lambda k: (r['business_id'][k], r['date'][k]))

    print("GO!")
    with open('../output/testing.txt', 'w') as f:
        for i in index:
            if r['business_id'][i] in tb:
                f.write("%s %s %s %f\n" % (r['user_id'][i], r['business_id'][i], r['date'][i].strftime("%Y/%m/%d"), r['stars'][i]))


if __name__ == '__main__':
    # split_training_data('../data/training.txt', 1000, 100)
    generate_blablabla()