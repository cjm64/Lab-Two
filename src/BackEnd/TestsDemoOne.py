import unittest
import BackEnd.JakesFunctions
import BackEnd.OurClasses


class MyTestCase(unittest.TestCase):

    def test_hitDetection(self):

        case1 = BackEnd.OurClasses.thing(10, 10) # x and y
        case1.sizeX = 4
        case1.sizeY = 4

        case1p = BackEnd.OurClasses.thing(8, 15) # this position is outside of the player's range
        case1p.sizeX = 3 # this puts it in x range
        case1p.sizeY = 10 # below the player

        case2 = BackEnd.OurClasses.thing(4, 3)
        case2.sizeX = 6
        case2.sizeY = 2

        case2p = BackEnd.OurClasses.thing(9, 1)
        case2p.sizeX = 5
        case2p.sizeY = 1

        case3 = BackEnd.OurClasses.thing(31, 28)
        case3.sizeX = 4
        case3.sizeY = 2

        case3p = BackEnd.OurClasses.thing(33, 31)
        case3p.sizeX = 20
        case3p.sizeY = 15

        case4 = BackEnd.OurClasses.thing(10, 15)
        case4.sizeX = 10
        case4.sizeY = 5

        case4p = BackEnd.OurClasses.thing(5, 5)
        case4p.sizeX = 10
        case4p.sizeY = 4

        case5 = BackEnd.OurClasses.thing(8, 10)
        case5.sizeX = 30
        case5.sizeY = 6

        case5p = BackEnd.OurClasses.thing(10, 5)
        case5p.sizeX = 2
        case5p.sizeY = 8

        case6 = BackEnd.OurClasses.thing(20, 20)
        case6.sizeX = 5
        case6.sizeY = 10

        case6p = BackEnd.OurClasses.thing(18, 25)
        case6p.sizeX = 4
        case6p.sizeY = 10

        case7 = BackEnd.OurClasses.thing(30, 30)
        case7.sizeX = 20
        case7.sizeY = 20

        case7p = BackEnd.OurClasses.thing(40, 40)
        case7p.sizeX = 5
        case7p.sizeY = 5

        case8 = BackEnd.OurClasses.thing(5, 5)
        case8.sizeX = 5
        case8.sizeY = 5

        case8p = BackEnd.OurClasses.thing(15, 5)
        case8p.sizeX = 5
        case8p.sizeY = 5

        self.assertEqual(BackEnd.JakesFunctions.hitDetection(case1, case1p), False)
        self.assertEqual(BackEnd.JakesFunctions.hitDetection(case2, case2p), False)
        self.assertEqual(BackEnd.JakesFunctions.hitDetection(case3, case3p), False)
        self.assertEqual(BackEnd.JakesFunctions.hitDetection(case4, case4p), False)
        self.assertEqual(BackEnd.JakesFunctions.hitDetection(case5, case5p), True)
        self.assertEqual(BackEnd.JakesFunctions.hitDetection(case6, case6p), True)
        self.assertEqual(BackEnd.JakesFunctions.hitDetection(case7, case7p), True)
        self.assertEqual(BackEnd.JakesFunctions.hitDetection(case8, case8p), False)

    def test_offMapDetection(self):

        world = BackEnd.OurClasses.theWorld
        world.boundaryX = 800
        world.boundaryY = 800

        case1 = BackEnd.OurClasses.thing(-2, 400)
        case2 = BackEnd.OurClasses.thing(1000, 400)
        case3 = BackEnd.OurClasses.thing(400, -40)
        case4 = BackEnd.OurClasses.thing(400, 1500)
        case5 = BackEnd.OurClasses.thing(-1000, -1000)
        case6 = BackEnd.OurClasses.thing(1000, 1000)
        case7 = BackEnd.OurClasses.thing(0, 0) # false
        case8 = BackEnd.OurClasses.thing(800, 800)
        case9 = BackEnd.OurClasses.thing(800, 400)
        case10 = BackEnd.OurClasses.thing(400, 400)

        self.assertEqual(BackEnd.JakesFunctions.offMapDetection(case1, world), True)
        self.assertEqual(BackEnd.JakesFunctions.offMapDetection(case2, world), True)
        self.assertEqual(BackEnd.JakesFunctions.offMapDetection(case3, world), True)
        self.assertEqual(BackEnd.JakesFunctions.offMapDetection(case4, world), True)
        self.assertEqual(BackEnd.JakesFunctions.offMapDetection(case5, world), True)
        self.assertEqual(BackEnd.JakesFunctions.offMapDetection(case6, world), True)
        self.assertEqual(BackEnd.JakesFunctions.offMapDetection(case7, world), False)
        self.assertEqual(BackEnd.JakesFunctions.offMapDetection(case8, world), False)
        self.assertEqual(BackEnd.JakesFunctions.offMapDetection(case9, world), False)
        self.assertEqual(BackEnd.JakesFunctions.offMapDetection(case10, world), False)



    def test_UpdatePosition(self):

        testThing = BackEnd.OurClasses.thing(80000, 800000)

        x1 = 30
        y1 = 40
        x2 = 68000
        y2 = 45

        BackEnd.JakesFunctions.updatePosition(testThing, x1, y1)
        self.assertEqual(testThing.x, 30)
        self.assertEqual(testThing.y, 40)

        BackEnd.JakesFunctions.updatePosition(testThing, x2, y2)
        self.assertEqual(testThing.x, 68000)
        self.assertEqual(testThing.y, 45)


    def test_createNewPlayer(self):

        location1 = BackEnd.OurClasses.thing(40, 21)
        name1 = "newPlayer"

        location2 = BackEnd.OurClasses.thing(33, 77)
        name2 = "Godzilla"

        location3 = BackEnd.OurClasses.thing(90, 32)
        name3 = "Jake"

        BackEnd.JakesFunctions.createNewPlayer(location1, name1)
        self.assertEqual(BackEnd.JakesFunctions.playerList[0].name, "newPlayer")
        self.assertEqual(BackEnd.JakesFunctions.playerList[0].x, 40)
        self.assertEqual(BackEnd.JakesFunctions.playerList[0].y, 21)

        BackEnd.JakesFunctions.createNewPlayer(location2, name2)
        self.assertEqual(BackEnd.JakesFunctions.playerList[1].name, "Godzilla")
        self.assertEqual(BackEnd.JakesFunctions.playerList[1].x, 33)
        self.assertEqual(BackEnd.JakesFunctions.playerList[1].y, 77)

        BackEnd.JakesFunctions.createNewPlayer(location3, name3)
        self.assertEqual(BackEnd.JakesFunctions.playerList[2].name, "Jake")
        self.assertEqual(BackEnd.JakesFunctions.playerList[2].x, 90)
        self.assertEqual(BackEnd.JakesFunctions.playerList[2].y, 32)


if __name__ == '__main__':
    unittest.main()
