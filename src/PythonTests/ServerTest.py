import unittest
from websocket
import json




class UnitTesting(unittest.TestCase):
    def test_register(self):
        #ws = create_connection("ws://127.0.0.1:8023/socket.io/?EIO=3&transport=websocket")
        name = "testing Functionality"
        jsonString = json.dumps({
            'name': name,
            'vertical': 0,
            'horizontal': 0,
            'angle': 100
        })
        ws.send("register")
        rawString = ws.recv()
        print(rawString)
        parsed = json.loads(rawString)
        message = parsed["data"]
        self.assertTrue(message == "registered testing Functionality")

def on_message(ws, message):
    message = JSON.parse(event)
    if(message["type"] == "register"){
    console.log(message["data"])
    }
    else if(message["type"] == "game"){
    gameState = message["data"]
    }
    else{
        console.log("unknown data type")
    }
    print(message)

def on_error(ws, error):
    print(error)

def on_close(ws):
    print("### closed ###")

if __name__ == '__main__':
    ws = websocket.WebSocketApp("ws://127.0.0.1:8023/socket.io/?EIO=3&transport=websocket",
                                on_message = on_message,
                                on_error = on_error,
                                on_close = on_close)
    unittest.main()