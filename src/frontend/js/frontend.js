function startGame() {
    new Phaser.Game(config);
}

var name;
var gameState;
var initializedObject;

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

gameState = {"PLayerData":[{
        "Name":"test",
        "x": 350,
        "y": 350,
        "Kills":2,
        "Projectile":[]
    }]}


function MyButton(){
    var control = document.getElementById('controls');
    var modal = document.getElementById('myModal');
    name = document.getElementById('username').value;
    console.log(name)
    console.log(gameState)
    if(isUsed(name)){
        document.getElementById('choose').innerHTML = name + " is already taken<br />"
    }
    else{
        jason["name"] = name
        modal.style.display = "none";
        socket.emit("register", name, JSON.stringify(jason))
        startGame()
        control.style.display = "block";

    }
}

var socket = io.connect({transports: ['websocket']});
socket.on('connect', function (event) {
    // console.log("connected")
});
socket.on('message', function (event) {
    // received a message from the server
    // console.log(event);
    message = JSON.parse(event)
    if(message["type"] == "register"){
        console.log(message["data"])
    }
    else if(message["type"] == "game"){
        gameState = message["data"]
    }
    else{
        // console.log("unknown data type")
    }
});

var jason = {
    'name' : name,
    'vertical' : 0,
    'horizontal' : 0,
    'angle': 100
}

var lastJason = jason;

var parsedbtf = {}
var listofplayers = []

var config = {
    "type" : Phaser.AUTO,
    "width" : 800,
    "height" : 800,
    physics: {
        default: 'arcade',
        arcade: {
            gravity: { y: 0 },
            debug: false
        }
    },
    "scene" : {
        preload : preload,
        create : create,
        update : update,
        extend: {
            player: null,
            healthpoints: null,
            reticle: null,
            moveKeys: null,
            playerProjectiles: null,
            enemyProjectiles: null,
            time: 0
        }
    }
}; //More Phaser Stuff

var player;
//var scoreText;
var fire = 0;
//var board;
//var board1;

var movecam = false;

//More Phaser Stuff
var test = 0

function preload() {
    this.load.image('Player', 'assets/player.png');
    this.load.image('Shot', 'assets/shot.png');
    this.load.image('Enemy', 'assets/enemy.png');
    this.load.image('EnemyProjectile', 'assets/bullet.png');
    this.load.image('Background', 'assets/bg.png');
}

function create () {

    enemies = this.physics.add.group({classType: otherUsers, runChildUpdate: true });
    enemyShots = this.physics.add.group({classType: Shot, runChildUpdate: true });
    Shots = this.physics.add.group({classType: MyShots, runChildUpdate: true });
    //Creates sprite classes for player bullets and bullets.

    for (y = -3; y < 3; y++)
    {
        for (x = -3; x < 3; x++)
        {
            this.add.image(800 * x, 800 * y, 'Background').setOrigin(0).setAlpha(1);
        }
    }

    player = this.physics.add.sprite(400, 400, 'Player');

    player.setCollideWorldBounds(false);
    //Phaser function so that player cannot bypass world bounds

    this.input.on('pointerdown', function (pointer) {
        //Only shoots if the player has not fired in the past 20 frames.
        if (fire > 20) {
            fire = 0
            var bullet = Shots.get().setActive(true).setVisible(true)
            bullet.shoot(player, pointer)
            jason["angle"] = bullet.angle
        }
    }, this); //Sets the angle of the player's projectile when the mouse is clicked.

//    scoreText = this.add.text(16, 16, 'Your Kills: 0', { fontSize: '32px', fill: '#000' });
//    board = this.add.text(550, 16, 'Top Player', { fontSize: '32px', fill: '#000' });
//    board1 = this.add.text(550, 66, 'Nobody: 0', { fontSize: '32px', fill: '#000' });

    this.cameras.main.startFollow(player, true);
    this.cameras.main.setDeadzone(0, 0);
    this.cameras.main.setZoom(1);
    this.cameras.main.width = 800
    this.cameras.main.height = 800

}
var clock = 0;
var ve;
var ho;
var jangle;

function update(time, delta){

    var tabKey = this.input.keyboard.addKey('TAB')
    var scoreboard = document.getElementById('leaderboard');
    if(tabKey.isDown){
        scoreboard.style.display = "block"
        // console.log("q pressed")
    } else {
        scoreboard.style.display = "none"
    }

    parsedbtf = gameState
    // console.log(parsedbtf)
    listofplayers = parsedbtf["PlayerData"]
    top_kill = 0
    top_player = "you"
    for(var i = 0; i < listofplayers.length; i++){
        if(listofplayers[i]["Kills"] > parseFloat(top_kill)){
            top_kill = listofplayers[i]["Kills"]
            top_player = listofplayers[i]["Name"]
        }
        if(listofplayers[i]["Name"] == name){
            player.x = listofplayers[i]["x"]
            player.y = listofplayers[i]["y"]
            document.getElementById("myscore").innerHTML = listofplayers[i]["Kills"]
            for(var q = 0; q < listofplayers[i]["Projectile"].length; q++){
                var mb = Shots.get().setActive(true).setVisible(true);
                mb.x = listofplayers[i]["Projectile"][q]["x"]
                mb.y = listofplayers[i]["Projectile"][q]["y"]
            }
            for(var k = 0; k < listofplayers.length; k++){
                if(listofplayers[k]["Name"] != name) {
                    if (Math.abs(player.x - listofplayers[k]["x"]) < 500 && Math.abs(player.y - listofplayers[k]["y"]) < 500) {
                        var xd = enemies.get().setActive(true).setVisible(true);
                        xd.x = listofplayers[k]["x"]
                        xd.y = listofplayers[k]["y"]
                    }
                    for (var j = 0; j < listofplayers[k]["Projectile"].length; j++) {
                        if (Math.abs(player.x - listofplayers[k]["Projectile"][j]["x"]) < 500 && Math.abs(player.y - listofplayers[k]["Projectile"][j]["y"]) < 500) {
                            var gun = enemyShots.get().setActive(true).setVisible(true);
                            gun.x = listofplayers[k]["Projectile"][j]["x"]
                            gun.y = listofplayers[k]["Projectile"][j]["y"]
                        }
                    }

                }
            }
        }

        if(listofplayers[i]["Name"] != name) {
            if (Math.abs(player.x - listofplayers[i]["x"]) < 500 && Math.abs(player.y - listofplayers[i]["y"]) < 500) {
                var xd = enemies.get().setActive(true).setVisible(true);
                xd.x = listofplayers[i]["x"]
                xd.y = listofplayers[i]["y"]
            }
            for (var j = 0; j < listofplayers[i]["Projectile"].length; j++) {
                if (Math.abs(player.x - listofplayers[i]["Projectile"][j]["x"]) < 500 && Math.abs(player.y - listofplayers[i]["Projectile"][j]["y"]) < 500) {
                    var gun = enemyShots.get().setActive(true).setVisible(true);
                    gun.x = listofplayers[i]["Projectile"][j]["x"]
                    gun.y = listofplayers[i]["Projectile"][j]["y"]
                }
            }
        }
    }
    document.getElementById("score").innerHTML = top_player + ": " + top_kill.toString()


    var cursors = this.input.keyboard.addKeys(
        {up: Phaser.Input.Keyboard.KeyCodes.W,
            down: Phaser.Input.Keyboard.KeyCodes.S,
            left: Phaser.Input.Keyboard.KeyCodes.A,
            right: Phaser.Input.Keyboard.KeyCodes.D
        });

    if (cursors.left.isDown) {
        jason['horizontal'] = -1;
    }
    else if (cursors.right.isDown) {
        jason['horizontal'] = 1;
    }
    else {
        jason['horizontal'] = 0;
    }
    if (cursors.up.isDown) {
        jason['vertical'] = -1;
    }
    else if (cursors.down.isDown) {
        jason['vertical'] = 1;
    }
    else {
        jason['vertical'] = 0;
    }
    if (cursors.up.isDown && cursors.down.isDown) {
        jason['vertical'] = 0;
    }
    if (cursors.left.isDown && cursors.right.isDown) {
        jason['horizontal'] = 0;
    }

    // console.log(clock)
    /*if(clock == 0){
        // console.log("sent")
        socket.emit("Jason", JSON.stringify(jason));
        jason["angle"] = 100;
    }
    else if(clock == 3){
        clock = -1;
    }
    clock += 1*/

    if(ve != jason["vertical"] || ho != jason["horizontal"] || jangle != jason["angle"]){
        console.log("sent")
        socket.emit("Jason", JSON.stringify(jason));
    }

    ve = jason["vertical"];
    ho = jason["horizontal"];
    jangle = jason["angle"];


    jason["angle"] = 100

    fire +=1;
    return;
}

var otherUsers = new Phaser.Class({
    Extends: Phaser.GameObjects.Image,
    initialize:
        function User (scene)
        {
            Phaser.GameObjects.Image.call(this, scene, 0, 0, 'Enemy');
            this.xSpeed = 0;
            this.ySpeed = 0;
            this.age = 0;
        },
    update: function () {
        this.age += 1
        if(this.age > 0){
            this.destroy()
        }
        this.x += this.xSpeed;
        this.y += this.ySpeed;
    }
});

var Shot = new Phaser.Class({
    Extends: Phaser.GameObjects.Image,
    initialize:
        function Shot (scene)
        {
            Phaser.GameObjects.Image.call(this, scene, 0, 0, 'EnemyProjectile');
            this.xSpeed = 0;
            this.ySpeed = 0;
            this.angle = 0;
            this.age = 0;
            this.mult = 0;
        },
    shoot: function (int, dest)
    {
        // Initial position is set to the object shooting's position
        this.x = int.x
        this.y = int.y
        if(dest.y < 400){
            this.mult = 3.14159
        }
        this.angle = Math.atan( (dest.x-(config['width'] / 2)) / (dest.y-(config['height'] / 2))) + this.mult;
    },
    update: function () {
        this.age += 1
        if(this.age > 0){
            this.destroy()
        }
        this.x += this.xSpeed;
        this.y += this.ySpeed;
    }
});

var MyShots = new Phaser.Class({
    Extends: Phaser.GameObjects.Image,
    initialize:
        function MyShots (scene)
        {
            Phaser.GameObjects.Image.call(this, scene, 0, 0, 'Shot');
            this.xSpeed = 0;
            this.ySpeed = 0;
            this.angle = 0;
            this.age = 0;
            this.mult = 0;
        },
    shoot: function (int, dest)
    {
        // Initial position is set to the object shooting's position
        this.x = int.x
        this.y = int.y
        if(dest.y < 400){
            this.mult = 3.14159
        }
        this.angle = Math.atan( (dest.x-(config['width'] / 2)) / (dest.y-(config['height'] / 2))) + this.mult;
    },
    update: function () {
        this.age += 1
        if(this.age > 0){
            this.destroy()
        }
        this.x += this.xSpeed;
        this.y += this.ySpeed;
    }
});