__author__ = 'bingo4508'


def predict(g, adopters):
    C = set([])
    for e in adopters:
        C |= set(g.neighbors(e))
    C2 = C.copy()
    for e in C:
        C2 |= set(g.neighbors(e))
    d = {}
    for e in C2:
        d[e] = len(set(g.neighbors(e)) & set(adopters))
    d = sorted(d.items(), key=lambda (k, v): v, reverse=True)
    return [k for k, v in d[:100]]



