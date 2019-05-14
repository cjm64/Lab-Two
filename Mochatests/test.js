var assert = require('assert');
var myVal = "hold";
var jason = {
    'name' : "test",
    'vertical' : 0,
    'horizontal' : 0,
    'angle': 100
}
var name = "test"
var timer = "on";

gameState = {"PlayerData":[{
        "Name":"test",
        "x": 350,
        "y": 350,
        "Kills":2,
        "Projectile":[]
}]}

function resolveServer() {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve('resolved');
    }, 1000);
  });
}

function isUsed(name){
    if(gameState["PlayerData"] != undefined){
        for(var y = 0; y < gameState["PlayerData"].length; y++){
            console.log("compare: " + gameState["PlayerData"][y]["Name"] + " and "+ name)
            if(gameState["PlayerData"][y]["Name"] == name){
                return true
            }
        }
        return false
    }
    return false

}

describe('Functionality', function() {
    describe('#isUsed()', function() {
        it("isUsed Function returns true when used", function(){
            assert.strictEqual(isUsed(name), true)
        })
        it("isUsed Function returns false when not used", function(){
            assert.strictEqual(isUsed("myName"), false)
        })
    });
});

describe('Server', function() {
    describe('#indexOf()', function() {
        var socket = require('socket.io-client')('http://localhost:8505', {transports:['websocket']});
        socket.on('connect', function (event) {
            // console.log("connected")
        });
        socket.on('message', function (event) {
            // received a message from the server
            // console.log(event);
            message = JSON.parse(event)
            if(message["type"] == "register"){
                myVal = message["data"]
            }
            else if(message["type"] == "game"){
                gameState = message["data"]
            }
            else{
                // console.log("unknown data type")
            }

        });
        socket.emit("register", name, JSON.stringify(jason))

        it('Connects to Socket Server', async function() {
            var result = await resolveServer();


            assert.strictEqual(myVal, "registered test")
        });
    });
});

