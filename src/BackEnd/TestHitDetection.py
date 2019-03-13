
import unittest
import BackEnd.OurClasses


class MyTestCase(unittest.TestCase):
    def testHitDetection(self):

        player = BackEnd.OurClasses.thing(10, 10)
        player.sizeX = 20
        player.sizeY = 20

        projectile = BackEnd.OurClasses.thing(8, 15) # this position is outside of the player's range
        projectile.sizeX = 1
        projectile.sizeY = 10


        self.assertEqual(True, False)


if __name__ == '__main__':
    unittest.main()
