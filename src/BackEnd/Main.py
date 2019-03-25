import BackEnd.JakesFunctions
import BackEnd.OurClasses
import time
import sqlite3

breakLoop = False
currentTime = 0.0

conn = sqlite3.connect('data.db')
cur = conn.cursor()
cur.execute('CREATE TABLE IF NOT EXISTS players (playerName, sizeX, sizeY, x, y)')

cur.execute('CREATE TABLE IF NOT EXISTS projectiles (projectileID, sizeX, sizeY, x, y)')

cur.execute('INSERT INTO players VALUES ("Jerry", 20, 20, 54, 36)')

cur.execute('INSERT INTO projectiles VALUES ("993", 10, 10, 49, 230)')

# players and projectiles are stored in separate tables

# we still need a function that basically determines a linear function for a projectile

# this loop will take a json string and update the database (adding new players/rows)

# size and position will not be determined by the json string

# (to my knowledge) the json string will contain: {}

# we MIGHT want to put another column in the table to determine if it is a projectile or not

# code in this block will execute every 1 seconds (not including runtime)

while True:

    player = cur.execute('SELECT * FROM players WHERE playerName=?',("Jerry",))
    infoList = []
    for i in player:
        infoList.append(i)

    print(infoList)

    if breakLoop:
        break

    time.sleep(1)
    currentTime += 1

conn.close()
