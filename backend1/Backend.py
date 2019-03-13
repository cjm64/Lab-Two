import random
#p1 = {"name": "", "kills": 0, "deaths": 0}
#lst = [{"name": "", "kills": 0, "deaths": 0}, {"name": "", "kills": 0, "deaths": 0}]


def elimination(hit, p1, p2):
    fin = []
    if hit == True:
        p1["kills"] += 1
        fin.append(p1)
        p2["deaths"] += 1
        fin.append(p2)
    return fin


def scoreBoard(lst):
    topPlayer = lst[0]["kills"]
    pName = lst[0]["name"]
    for i in lst:
        c = i["kills"]
        if c >= topPlayer:
            pName = i["name"]
    return pName


def spawnLocation():
    loc = []
    for i in range(1):
        x = random.randint(0, 801)
        loc.append(x)
    for e in range(1):
        y = random.randint(0, 801)
        loc.append(y)
    return loc


if __name__ == '__main__':
    print(spawnLocation())
    print(elimination(True, {"name": "", "kills": 0, "deaths": 0}, {"name": "", "kills": 0, "deaths": 0}))
    print(scoreBoard([{"name": "player1", "kills": 1, "deaths": 0}, {"name": "player2", "kills": 2, "deaths": 0}]))
