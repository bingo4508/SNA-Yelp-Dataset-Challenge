__author__ = 'bingo4508'
from src.util import *
import os


def __increment_dict(d, e, delta=1):
    if e in d:
        d[e] += delta
    else:
        d[e] = delta


def __delete_keys(d, keys):
    delete = []
    for k in keys:
        if k in d:
            delete.append(k)

    for e in set(delete):
        del d[e]


def predict(adopters, m_dir):
    x = {}
    for a in adopters:
        try:
            with open('%s/%s.txt' % (m_dir, a), 'r') as f:
                for l in f:
                    __increment_dict(x, l.strip())
        except Exception:
            pass

    if len(x) == 0:
        return []

    __delete_keys(x, adopters)

    # Second round
    # y = sorted(x.iteritems(), key=lambda (k, v): v, reverse=True)
    # y = [k for k, v in y[:10]]
    # for a in y:
    #     try:
    #         with open('%s/%s.txt' % (m_dir, a), 'r') as f:
    #             for l in f:
    #                 __increment_dict(x, l.strip())
    #     except Exception:
    #         pass
    #
    # if len(x) == 0:
    #     return []
    #
    # __delete_keys(x, adopters)


    candidates = sorted(x.iteritems(), key=lambda (k, v): v, reverse=True)
    last = len(candidates)
    return [k for k, v in candidates[:100 if last > 100 else last]]


def train(input_file='../../data/training3.txt'):
    window_size = 100

    t = load_idea(input_file)
    ideas = list(set(t['idea']))

    for m in range(len(ideas)):
        print m
        n = get_node_by_idea2(t, ideas[m])
        index = sorted(range(len(n['date'])), key=lambda k: n['date'][k])
        # date = [n['date'][i] for i in index]
        node = [n['node'][i] for i in index]

        for i, a in enumerate(node):
            try:
                # Influence the window_size nodes behind node[i]
                last = i+window_size if i+window_size < len(node)-i else len(node)-i
                x = [node[e] for e in range(i+1, last)]

                output = os.path.join(OUTPUT, 'naive3_%d' % window_size)
                if not os.path.exists(output):
                    os.makedirs(output)
                if len(x) > 0:
                    with open(os.path.join(output, '%s.txt' % a), 'a') as f:
                        for e in x:
                            f.write('%s\n' % e)
            except Exception:
                pass


if __name__ == '__main__':
    train()