import json
import datetime
import math


def load_json(fn, is_review=False, has_date=False):
    d = {}
    with open(fn, 'r') as f:
        for l in f:
            x = json.loads(l)
            if is_review:
                del x['text']
            if has_date:
                x['date'] = datetime.datetime.strptime(x['date'], '%Y-%m-%d')
            for k, v in x.items():
                if k not in d:
                    d[k] = [v]
                else:
                    d[k].append(v)
    return d


def dump(fn, li):
    with open(fn, 'w') as f:
        for e in li[0].keys():
            f.write(e+'\t')
        f.write('\n')
        for e in li:
            for k, v in e.items():
                f.write(u"{0}\t".format(v))
            f.write('\n')


def get_attr(d, attr, key, val):
    return d[attr][d[key].index(val)]


def increment_dict(d, e, delta=1):
    if e in d:
        d[e] += delta
    else:
        d[e] = delta


def append_dict(d, k, v):
    if k in d:
        d[k].append(v)
    else:
        d[k] = [v]