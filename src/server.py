import asyncio
import json
import websockets
import random

users = set()
usernames = []
playerNum = 0
randomNumbers = [0]
websocketIndex = {}

def users_event():
    #count = 0
    #for username in usernames:
    list = websocketIndex.values()
    newlist = []
    for item in list:
        newlist.append(item)
    print(newlist)
    return json.dumps({'type': 'users', 'users': newlist, "online": len(users)})


async def notify_users():
    if users:
        message = users_event()
        await asyncio.wait([user.send(message) for user in users])


async def register(websocket):
    """count = 0
    for username in usernames:
        print(username)
        if username == None:
            length = count
            break
        count +=1
        print(count)
    if count == len(usernames):
        length = len(usernames)
        NewestUser = length
        player = "Player %s" % length
        usernames.append(player)
        print("user: " + player)
    else:
        NewestUser = length"""
    length = len(usernames)
    randNum = random.randint(0,999)
    used = False
    while used:
        used = False
        for rand in randomNumbers:
            if rand == randNum:
                used = True
                randNum = random.randint(0, 999)
                break
    player = "Player %s" % randNum
    users.add(websocket)
    websocketIndex[websocket] =  player
    usernames.append(player)
    print("user: " + player)
    await notify_users()


async def unregister(websocket):
    num = 0
    we = websocketIndex.keys()
    print(we)
    for thing in we:
        if thing == websocket:
            print("ys")
            del websocketIndex[websocket]
            break
        print("here1")
        num += 1
    users.remove(websocket)
    await notify_users()


async def NameController(websocket, path):
    # register(websocket) sends user_event() to websocket
    await register(websocket)
    try:
        await websocket.send(users_event())
        async for message in websocket:
            data = json.loads(message)
            if data['action'] == 'names':
                await notify_users()
            elif data['action'] == 'name':
                if data['name'] is not None:
                    location = data['location']
                    usernames[location] = data['name']
                    websocketIndex[websocket] = data['name']
                await notify_users()
    finally:
        await unregister(websocket)


asyncio.get_event_loop().run_until_complete(
    websockets.serve(NameController, 'localhost', 6789))
asyncio.get_event_loop().run_forever()