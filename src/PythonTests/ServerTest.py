import unittest
import websocket
from websocket import create_connection




class UnitTesting(unittest.TestCase):
    ws = create_connection("127.0.0.1:8073")
    ws.send("register", )



if __name__ == '__main__':
    unittest.main()