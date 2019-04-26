"""


class Person:
  def __init__(self, name, age):
    self.name = name
    self.age = age

p1 = Person("John", 36)

print(p1.name)
print(p1.age)

"""
import pykka
import sqlite3
import json
import backend1.Backend


class theWorld:
    boundaryX = None
    boundaryY = None


class thing:

    name = ""
    sizeX = None
    sizeY = None

    # direction = None # 1 or -1

    # we can add like an image reference here too like: image = "anImage.png"

    def __init__(self, x, y):
        self.x = x
        self.y = y

"""
receiving json:

var jason = {
    'name' : name,
    'vertical' : 0,
    'horizontal' : 0,
    'angle': null
}
"""

class receivingActor(pykka.ThreadingActor):

    # this information will be given by the constructor
    # conn = sqlite3.connect('data.db')
    # cur = conn.cursor()

    def __init__(self, cur):
        super(receivingActor, self).__init__()
        self.cur = cur
        self.cur.execute('CREATE TABLE IF NOT EXISTS players (playerName, sizeX, sizeY, x, y)')
        self.cur.execute('CREATE TABLE IF NOT EXISTS projectiles (projectileID, sizeX, sizeY, x, y)')
        self.cur.execute('CREATE TABLE IF NOT EXISTS world (boundaryX, boundaryY)')
        # perhaps unnecessary
        self.cur.execute('INSERT INTO world VALUES (800, 800)')
        worldList = self.cur.execute('SELECT * FROM world')
        # perhaps unnecessary
        self.worldX = worldList[0]
        self.worldY = worldList[1]




    def on_receive(self, theJson):
        # updates the database
        # {"name": [String], "vertical": [Int], "horizontal": [Int], "angle": [Double?]}
        # this is the json from the server

        # if the player is already in the table, just add on the vertical & horizontal
        # if the player isn't in the table, create a random spawn location
        jsonLoaded = json.loads(theJson)
        # select all the names in the player table
        if jsonLoaded["name"] in self.cur.execute('SELECT playerName FROM players'):
            # I think this is correct syntax...
            thatRow = self.cur.execute('SELECT * FROM players WHERE playerName=?', (jsonLoaded["name"]))
            currentX = thatRow[3]
            currentY = thatRow[4]
            self.cur.execute('UPDATE players SET x=?, y=?, WHERE playerName=?', (currentX + jsonLoaded["horizontal"], currentY + jsonLoaded["vertical"], jsonLoaded["name"]))
        else:
            xyList = backend1.Backend.spawnLocation()
            self.cur.execute('INSERT INTO players VALUES (?, 20, 20, ?, ?)', (jsonLoaded["name"], xyList[0], xyList[1]))

        # now the angle...
        # if angle != null, then create a new projectile with corresponding information




    def initializeDatabase(self):
        self.cur.execute('CREATE TABLE IF NOT EXISTS players (playerName, sizeX, sizeY, x, y)')
        self.cur.execute('CREATE TABLE IF NOT EXISTS projectiles (projectileID, sizeX, sizeY, x, y)')
        self.cur.execute('CREATE TABLE IF NOT EXISTS world (boundaryX, boundaryY)')
        self.cur.execute('INSERT INTO world VALUES (800, 800)')
        # self.cur.execute('INSERT INTO players VALUES ("Jerry", 20, 20, 54, 36)')
        # self.cur.execute('INSERT INTO projectiles VALUES ("993", 10, 10, 49, 230)')


# maybe have sending actor "ask()" receiving actor for information


class sendingActor(pykka.ThreadingActor):

    def __init__(self, cur):
        super(sendingActor, self).__init__()
        self.cur = cur
        worldList = self.cur.execute('SELECT * FROM world')
        self.worldX = worldList[0]
        self.worldY = worldList[1]

    def readDatabase(self):
        self.cur.execute()
        # table - world
        # table - projectiles
        # table - players

        # this is where we send the data back to the server
        # all that is required here is reading the database and sending the updated information
        playerList = self.cur.execute('SELECT * FROM players')
        projectileList = self.cur.execute('SELECT * FROM projectiles')
