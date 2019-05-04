function startGame() {
    new Phaser.Game(config);
} //Phaser startgame

var name;
var gameState;

function test(){
    var modal = document.getElementById('myModal');
    var btn = document.getElementById("leaderboard");
    name = document.getElementById('username');
    modal.style.display = "none";
    startGame()
    /*if(isUsed(name)){
        document.getElementById('choose').innerHTML = name + " is already taken"
    }
    else{
        modal.style.display = "none";
        socket.emit("register", name, JSON.stringify(jason))
        startGame()
    }*/
}


var socket = require('socket.io-client')('http://localhost:8053');
socket.on('connect', function(){
    console.log('WebSocket Client Connected');
});
socket.on('event', function(data){
    console.log(data)
    gameState = message.parse()
});
socket.on('disconnect', function(){
    console.log('WebSocket Client Disconnected');
});

/*var WebSocketClient = require('websocket').client;

var client = new WebSocketClient();

client.on('connectFailed', function(error) {
    console.log("here")
    console.log('Connect Error: ' + error.toString());
});

client.on('connect', function(connection) {
    console.log('WebSocket Client Connected');
    connection.on('error', function(error) {
        console.log("Connection Error: " + error.toString());
    });
    connection.on('close', function() {
        console.log('echo-protocol Connection Closed');
    });
    connection.on('message', function(message) {
        if (message.type === 'utf8') {
            console.log("Received: '" + message.utf8Data + "'");
            gameState = message.parse()
        }
    });

});

client.connect('http://localhost:8053/');*/




/*var socket = io.connect({transports: ['websocket']});
socket.on('connect', function (event) {
    name = prompt("Please Enter a Username", "Username");
    while(isUsed(name)){
        name = prompt("Please Enter Another Username", name);
    };
    socket.emit("register", name)
});
socket.on('message', function (event) {
    // received a message from the server
    gameState = event.parse()
});*/


//Username input, repeats if blank
/*function nameself(){
   var name = prompt("Please Enter a Username", "Username");
    if (name == "") {
        nameself();
    }
}*/

//nameself();


var jason = {
    'name' : name,
    'vertical' : 0,
    'horizontal' : 0,
    'angle': null
} //Initializes the dictionary that will be used to send JSON to server.

var lastJason = jason;


var projectilelist = {

}

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
var enemy1;
var kills = 0;
var scoreText;
var hp = 0;
var hpText;
var deaths = 0;
var deathText;
var fire = 0;
var board;
var board1;
var board2;
var movecam = false;
var shotid = 0
//More Phaser Stuff


function preload(){
    this.load.image('Player', 'assets/player.png');
    this.load.image('Shot', 'assets/shot.png');
    this.load.image('Enemy', 'assets/enemy.png');
    this.load.image('EnemyProjectile', 'assets/bullet.png');
    this.load.image('Background', 'assets/bg.png');
}//More Phaser Stuff PT. 3

function create () {

    playerProjectiles = this.physics.add.group({ classType: Projectile, runChildUpdate: true });
    enemyProjectiles = this.physics.add.group({ classType: Projectile, runChildUpdate: true });
    //Creates classes for player bullets and enemy bullets, so that they don't shoot themselves. Unsure of how this might work in server, but probably easily doable.

    for (y = -1; y < 23; y++)
    {
        for (x = -1; x < 23; x++)
        {
            this.add.image(800 * x, 800 * y, 'Background').setOrigin(0).setAlpha(1);
        }
    }

    player = this.physics.add.sprite(400, 400, 'Player');
    //Creates Player

    player.setCollideWorldBounds(false);
    //Phaser function so that player cannot bypass world bounds

    this.input.on('pointerdown', function (pointer) {
        //Only shoots if the player has not fired in the past 20 frames.
        if (fire > 20) {
            fire = 0
            if (player.active === false)
                return;

            var bullet = playerProjectiles.get().setActive(true).setVisible(true);

            if (bullet) {
                bullet.shoot(player, pointer);
                //Projectile moves from shooter(player) to pointer(cursor).
                this.physics.add.collider(enemy1, bullet, enemyHitFunction);
                var cursorx = pointer.x
                var cursory = pointer.y
                var centerx = config['width'] / 2
                var centery = config['height'] / 2
                var angle = Math.atan( (cursorx - (config['width'] / 2)) / (cursory - (config['height'] / 2)));
                var shootvariables = {
                    'mousex' : 0,
                    'mousey' : 0,
                    'angle' : angle,
                    'camwidth' : config['width'] / 2,
                    'camheigh' : config['height'] / 2
                }
                jason["angle"] = angle
                var shootjson = JSON.stringify(shootvariables)

            }
        }
    }, this); //Shoots a bullet when the mouse is clicked.

    enemy1 = this.physics.add.sprite(100, 100, 'Enemy');
    //Creates an enemy

    enemy1.setCollideWorldBounds(true);
    //Example enemy can't move through world
    enemy1.setVelocity(Phaser.Math.Between(80, 200), Phaser.Math.Between(80, 200));
    //Randomize example enemy X and Y velocities
    enemy1.setBounce(1)
    //Example enemy bounces off wall

    // Set object variables
    player.health = 5;
    enemy1.health = 5;
    enemy1.lastFired = 0;

    //Scoreboard stuff. Should change to work with server and backend and stuff.

    //this.cameras.main.startFollow(player, true);
    //this.cameras.main.setDeadzone(0, 0);
    this.cameras.main.setZoom(1);
    this.cameras.main.width = 800
    this.cameras.main.height = 800


}


function update(time, delta){
    var cam = this.cameras.main;

    //It's the update function.

    //for(players in gameState["Playerdata"])



    var cursors = this.input.keyboard.createCursorKeys();
    //cam.x = player.x
    //cam.y = player.y
    if (cursors.left.isDown) {

        jason['horizontal'] = -1;
    }
    else if (cursors.right.isDown) {

        console.log("here")
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

    if(jason !== lastJason){
        client.send("Jason", json.stringify())
        lastJason = jason
    }

    // Above code sets variable "horizontal" and "vertical" in dictionary to 1 or 0 based on arrow key inputs
/*
    var jay = JSON.stringify(Movement);
    var par = JSON.parse(jay);

    if (par["W"] && par["S"]){
        player.y = 0;
    }
    else if(par["W"]){
        player.y = 4
    }
    else if(par["S"]){
        player.y = -4
    }
    else{
        player.y = 0
    }
    if (par["A"] && par["D"]){
        player.x = 0;
    }
    else if(par["D"]){
        player.x = 4
    }
    else if(par["A"]){
        player.x = -4
    }
    else{
        player.x = 0
    }
*/
    //player.x = player.x + (par['horizontal'] * 4);
    //player.y = player.y + (par['vertical'] * 4);
    //Sets position for X and Y based on JSON, which is based on arrow key input.
    //Could be set to do velocity instead, but then players would be able to set own position in console
    //Using position instead of velocity the player can sort of bypass world bounds up to its center.


    enemyFire(enemy1, player, time, this);
    //Enemy shoots a bullet whenever it's cooldown is over.

    fire +=1
    cam.x = player.x - 400
    cam.y = player.y - 400
    return;
}


//Class for projectiles. Handles how they act.
var Projectile = new Phaser.Class({

    Extends: Phaser.GameObjects.Image,

    initialize:

    // Constructor for projectiles
        function Projectile (scene)
        {
            Phaser.GameObjects.Image.call(this, scene, 0, 0, 'Shot');
            this.speed = 7.5;
            this.age = 0;
            this.angle = 0;
            this.xSpeed = 0;
            this.ySpeed = 0;
            this.setSize(16, 16, true);
        },

    //Fire function for projectile. Moves from object shooting to target
    shoot: function (x, y, angle)
    {
        // Initial position is set to the object shooting's position
        this.x = x
        this.y = y

        this.angle = angle
        // Calculate the xSpeed and ySpeed of projectile to moves it from initial position to target
        this.xSpeed = this.speed*Math.sin(this.angle);
        this.ySpeed = this.speed*Math.cos(this.angle);

        if (dest.y < config['height'] / 2)
        {
            this.xSpeed = -this.xSpeed
            this.ySpeed = -this.ySpeed
        }

        this.age = 0; // Age of projectile
        shotid +=1
    },

    // Updates the position and age of the projectile each cycle
    update: function () {
        this.age += 1;
        if (this.age > 120) {
            this.destroy();
            //projectile destroys intself after 120 frames
        }
        this.x += this.xSpeed;
        this.y += this.ySpeed;


    }

});


//Example collision code for demonstration.
function enemyHitFunction(enemyHit, projectileHit)
{
    // Randomize speed and direction after getting shot
    enemyHit.setVelocity(Phaser.Math.Between(80, 200), Phaser.Math.Between(80, 200));
    // Reduce Health of the Enemy
    if (projectileHit.active === true && enemyHit.active === true)
    {
        enemyHit.health = enemyHit.health - 1;

        // Kill enemy if health <= 0
        if (enemyHit.health <= 0)
        {
            enemyHit.x=Phaser.Math.Between(50, 750)
            enemyHit.y=Phaser.Math.Between(50, 750)
            enemyHit.health=5
            kills += 1;
            scoreText.setText('Kills: ' + kills);
            if (kills > deaths){
                board2.setText("Enemy: " + deaths)
                board1.setText(name + " : " + kills)
            }
            if (deaths > kills){
                board1.setText("Enemy: " + deaths)
                board2.setText(name + " : " + kills)
            }
        }

        // Destroy projectile
        projectileHit.destroy();
    }
}

//Example collision code for demonstration.
function playerHitFunction(playerHit, projectileHit)
{
    // Reduce health of player
    if (projectileHit.active === true && playerHit.active === true)
    {
        playerHit.health = playerHit.health - 1;
        hpText.setText('Health: ' + player.health)

        if (playerHit.health <= 0){
            playerHit.x=Phaser.Math.Between(50, 750)
            playerHit.y=Phaser.Math.Between(50, 750)
            playerHit.health=5
            deaths += 1;
            deathText.setText('Deaths: ' + deaths);
            hpText.setText('Health: ' + player.health)
            if (deaths > kills){
                board1.setText("Enemy: " + deaths)
                board2.setText(name + " :" + kills)
            }
            if (kills > deaths){
                board2.setText("Enemy: " + deaths)
                board1.setText(name + " : " + kills)
            }

        }

        // Destroy projectile
        projectileHit.destroy();
    }
}

//Enemy shoots. Probably won't be used.
function enemyFire(enemy1, player, time, gameObject)
{
    if (enemy1.active === false)
    {
        return;
    }

    if ((time - enemy1.lastFired) > 600)
    {
        enemy1.lastFired = time;

        // Get projectile from projectiles group
        var projectile = enemyProjectiles.get().setActive(true).setVisible(true);

        if (projectile)
        {
            projectile.shoot(enemy1, player);
            // Add collider between projectile and player
            gameObject.physics.add.collider(player, projectile, playerHitFunction);
        }
    }
}