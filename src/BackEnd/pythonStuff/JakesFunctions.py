import BackEnd
import BackEnd.pythonStuff.OurClasses

playerList = []
# this data should be stored in sql and not a list


World = BackEnd.pythonStuff.OurClasses.theWorld

player = BackEnd.pythonStuff.OurClasses.thing(10, 10)
player.sizeX = 20
player.sizeY = 20

projectile = BackEnd.pythonStuff.OurClasses.thing(8, 15) # this position is outside of the player's range
projectile.sizeX = 1
projectile.sizeY = 10 # because of this size however, it would be in the player's range


def hitDetection(thing1, thing2):
    if thing1.x > thing2.x + thing2.sizeX or thing2.x > thing1.x + thing1.sizeX:
        return False
    elif thing1.y > thing2.y + thing2.sizeY or thing2.y > thing1.y + thing1.sizeY:
        return False
    else:
        return True


def offMapDetection(thing, world):
    # returns true/false just like hit detection
    boundX = world.boundaryX
    boundY = world.boundaryY
    result = False
    if thing.x < 0 or thing.x > boundX:
        result = True
    elif thing.y < 0 or thing.y > boundY:
        result = True
    else:
        result = False
    return result


def updatePosition(thing, newX, newY):
    # uses info from sever to update the position of a "thing"
    thing.x = newX
    thing.y = newY


def createNewPlayer(locationThing, theName):
    # use random spawn location function from emily's backend
    # use "name" from justin's networking
    newPlayer = BackEnd.pythonStuff.OurClasses.thing(locationThing.x, locationThing.y)
    newPlayer.sizeX = 20
    newPlayer.sizeY = 20
    newPlayer.name = theName
    playerList.append(newPlayer)
