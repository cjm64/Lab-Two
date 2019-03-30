import BackEnd.JakesFunctions
import BackEnd.OurClasses
import backend1.Backend
import time
import sqlite3
import json

jsonString = ""
breakLoop = False
currentTime = 0.0
playerList = []

# create a new table for leaderboard

conn = sqlite3.connect('data.db')
cur = conn.cursor()
cur.execute('CREATE TABLE IF NOT EXISTS players (playerName, sizeX, sizeY, x, y)')

cur.execute('CREATE TABLE IF NOT EXISTS projectiles (projectileID, sizeX, sizeY, x, y)')

cur.execute('CREATE TABLE IF NOT EXISTS world (boundaryX, boundaryY)')

cur.execute('INSERT INTO world VALUES (800, 800)')

cur.execute('INSERT INTO players VALUES ("Jerry", 20, 20, 54, 36)')

cur.execute('INSERT INTO projectiles VALUES ("993", 10, 10, 49, 230)')

# players and projectiles are stored in separate tables

# we still need a function that basically determines a linear function for a projectile

# this loop will take a json string and update the database (adding new players/rows)

# size and position will not be determined by the json string

# (to my knowledge) the json string will contain: {}

# we MIGHT want to put another column in the table to determine if it is a projectile or not - edit: nope, just a table

# code in this block will execute every 1 seconds (not including runtime)

# BackEnd.JakesFunctions.hitDetection(thing1, thing2)
# BackEnd.JakesFunctions.offMapDetection(thing1, world)
# BackEnd.JakesFunctions.updatePosition(thing, newX, newY)
# BackEnd.JakesFunctions.createNewPlayer(locationThing, theName)
# backend1.Backend.elimination(hit, p1, p2)
# backend1.Backend.scoreBoard(lst)
# backend1.Backend.spawnLocation()
# BackEnd.OurClasses.thing(x, y)
#       - .name
#       - .sizeX
#       - .sizeY
# BackEnd.OurClasses.theWorld
#       - .boundaryX
#       - .boundaryY


while True:

    # first we want to update every "thing" and "theWorld"
    # "theWorld" is actually static but still update in case the server resets
    #


















    player = cur.execute('SELECT * FROM players WHERE playerName=?',("Jerry",))
    infoList = []
    for i in player:
        infoList.append(i)

    print(infoList)

    # update sql tables based on json

    # update running list of objects based on sql

    # from objects(things) we can update these and send a giant json to the server which then precedes
    # to send that to the front end
    if breakLoop:
        break

    time.sleep(1)
    currentTime += 1

conn.close()
