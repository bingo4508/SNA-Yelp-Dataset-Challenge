__author__ = 'bingo4508'

from util import *
import random as rd
import datetime


def generate_graph():
    users = load_json('data/User.txt')

    print("GO!")
    with open('output/graph.txt', 'w') as f:
        for i, u in enumerate(users['user_id']):
            if len(users['friends'][i]) > 0:
                f.write('%s %s\n' % (u, ' '.join(users['friends'][i])))


def generate_training():
    r = load_json('data/Review.txt', True, True)
    index = sorted(range(len(r['date'])), key=lambda k: r['date'][k])

    print("GO!")
    with open('output/training.txt', 'w') as f:
        for i in index:
            f.write("%s %s %s %f\n" % (r['user_id'][i], r['business_id'][i], r['date'][i].strftime("%Y/%m/%d"), r['stars'][i]))


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
        with open('output/test_data_q%d.txt' % i, 'w') as fq:
            with open('output/test_data_a%d.txt' % i, 'w') as fa:
                for k in d:
                    cut = int(len(d[k])*i*0.1)
                    fq.write(' '.join(d[k][:cut])+'\n')
                    fa.write(' '.join(d[k][cut:])+'\n')


def generate_training2():
    r = load_json('data/Review.txt', True, True)
    d = {}
    for i in range(len(r['business_id'])):
        increment_dict(d, r['business_id'][i])

    c = []
    for k in d:
        if d[k] > 100:
            c.append(k)

    # Random select 10 business having more than 100 reviews
    c = rd.sample(c, 100)
    
    index = sorted(range(len(r['business_id'])), key=lambda k: (r['business_id'][k], r['date'][k]))

    print("GO!")
    with open('output/training.txt', 'w') as f:
        for i in index:
            if r['business_id'][i] not in c:
                f.write("%s %s %s %f\n" % (r['user_id'][i], r['business_id'][i], r['date'][i].strftime("%Y/%m/%d"), r['stars'][i]))

    with open('output/testing_business.txt', 'w') as f:
        f.write(' '.join(c))

    # Generate 3 testing data
    generate_testing(r, c)


def generate_training3(date=datetime.datetime(2014, 1, 1)):
    r = load_json('data/Review.txt', True, True)
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
    with open('output/training.txt', 'w') as f:
        for i in index:
            if r['date'][i] < date:
                f.write("%s %s %s %f\n" % (r['user_id'][i], r['business_id'][i], r['date'][i].strftime("%Y/%m/%d"), r['stars'][i]))

    with open('output/testing_business.txt', 'w') as f:
        f.write(' '.join(c))

    # Generate 3 testing data
    generate_testing(r, c, date)


def generate_training4():
    r = load_json('data/Review.txt', True, True)
    d = {}
    for i in range(len(r['business_id'])):
        increment_dict(d, r['business_id'][i])

    c = []
    for k in d:
        if d[k] > 100:
            c.append(k)

    # Random select 10 business having more than 100 reviews
    c_t = rd.sample(c, 100)
    c = set(c)-set(c_t)

    index = sorted(range(len(r['business_id'])), key=lambda k: (r['business_id'][k], r['date'][k]))

    print("GO!")
    with open('output/training.txt', 'w') as f:
        for i in index:
            if r['business_id'][i] in c:
                f.write("%s %s %s %f\n" % (r['user_id'][i], r['business_id'][i], r['date'][i].strftime("%Y/%m/%d"), r['stars'][i]))

    with open('output/testing_business.txt', 'w') as f:
        f.write(' '.join(c))

    # Generate 3 testing data
    generate_testing(r, c_t)



if __name__ == '__main__':
    generate_training4()


# date = datetime.datetime(2014, 1, 1)
# d = {}
# q = 0
# for i in range(len(r['date'])):
#     if r['date'][i] > date:
#         q += 1
#         increment_dict(d, r['business_id'][i])
#
# c = 0
# for k, v in d.items():
#     if v > 100:
#         c += 1