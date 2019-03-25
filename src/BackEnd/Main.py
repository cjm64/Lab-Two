import BackEnd.JakesFunctions
import BackEnd.OurClasses
import time
import sqlite3

breakLoop = False
currentTime = 0.0

conn = sqlite3.connect('data.db')
cur = conn.cursor()
cur.execute('CREATE TABLE IF NOT EXISTS players (playerName, sizeX, sizeY, x, y)')
cur.execute('INSERT INTO players VALUES ("Jerry", 20, 20, 54, 36)')

# we MIGHT want to put another column in the table to determine if it is a projectile or not

# code in this block will execute every 0.1 seconds (not including runtime)
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
