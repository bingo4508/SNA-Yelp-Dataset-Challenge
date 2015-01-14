__author__ = 'bingo4508'
from util import *


u = load_json('data/User.txt')
r = load_json('data/Review.txt', True, True)
b = load_json('data/Business.txt')

# Map user and review
user_review = {}
for i in range(len(r['user_id'])):
    try:
        user_review[r['user_id'][i]].append((r['business_id'][i], r['date'][i]))
    except:
        user_review[r['user_id'][i]] = [(r['business_id'][i], r['date'][i])]


# Test how many percentage of overlap of one's reviews with one's friends review in the same cities
for i in range(10000):   # len(u['user_id'])
    u_review = user_review[u['user_id'][i]]
    business_cities = set([b['city'][b['business_id'].index(e)] for e in u_review])
    if u['fans'][i] > 10 and len(u_review) > 10:
        print "#review: {0}  #friends: {1} #Fans: {2}".format(len(u_review), len(u['friends'][i]), u['fans'][i])
        for c in business_cities:
            u_r = [e for e in u_review if get_attr(b, 'city', 'business_id', e) == c]
            overlap = []
            overlap_des = []
            for f in u['friends'][i]:
                f_r = [e for e in user_review[f] if get_attr(b, 'city', 'business_id', e) == c]
                if len(f_r) > 0:
                    overlap.append(float(len(set(f_r) & set(u_r))) / len(set(f_r)))
                    overlap_des.append('{0}/{1}'.format(float(len(set(f_r) & set(u_r))), len(set(f_r))))
            if len(overlap) != 0:
                # print
                print "Max: {0} - {1}     Avg: {2}".format(max(overlap), overlap_des[overlap.index(max(overlap))], float(sum(overlap))/len(overlap))
        raw_input("____________________________")


# Test many reviews in one's reviews appears in ones's friends earlier reviews
def test(num, min, max=10000, days_before=100000, debug=False):
    avg = []
    for i in range(num):   # len(u['user_id'])
        u_review = user_review[u['user_id'][i]]
        count = [0]*len(u_review)
        if max >= len(u['friends'][i]) >= min:
            # print "#review: {0}  #friends: {1} #Fans: {2}".format(len(u_review), len(u['friends'][i]), u['fans'][i])
            for j, (u_r, u_date) in enumerate(u_review):
                for f in u['friends'][i]:
                    for f_r, f_date in user_review[f]:
                        if f_r == u_r and u_date > f_date and (u_date-f_date).days <= days_before:
                            count[j] += 1
            avg.append(float(len(count)-count.count(0)) / len(count))
            if debug:
                print count
                raw_input('')
    return len(avg), sum(avg)/len(avg)


for k, v in user_review.items():
    print set([get_attr(b, 'city', 'business_id', e) for e in v])
    raw_input("...")