function hitDetection(thing1, thing2) {
    if (thing1.x - thing1.sizeX > thing2.x + thing2.sizeX || thing2.x - thing2.sizeX > thing1.x + thing1.sizeX) {
        return false
    }
    if (thing1.y - thing1.sizeY > thing2.y + thing2.sizeY || thing2.y - thing2.sizeY > thing1.y + thing1.sizeY ) {
        return false
    }
    return true
}

function startGame() {
    new Phaser.Game(config);

} //Phaser startgame


var name = ""
//Username input, repeats if blank
function nameself(){
   name = prompt("Please Enter a Username", "Username");
    if (name == "") {
        nameself();
    }
}

nameself();

var jason = {
    'name' : name,
    'vertical' : 0,
    'horizontal' : 0
} //Initializes the dictionary that will be used to send JSON to server.

var bulletlist = []
var entitylist = []


function del(item){
    for (var j = 0; j < bulletlist.length; j=j+1){
        if (item.a == bulletlist[j]["id"]){
            bulletlist.splice(j, 1)
        }
    }
    item.destroy()
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

// var scoreHTML = document.getElementById("score").innerHTML;
// var myscoreHTML = document.getElementById("myscore").innerHTML;
var player;
var enemy1;
var kills = 0;
var scoreText;
var hpText;
var deaths = 0;
var deathText;
var fire = 0;
var efire = 0;
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
    player.name = name
    var p = {
        "username": name,
        "x": player.x,
        "y": player.y,
        "sizeX": 16,
        "sizeY": 16,
        "health": 5
    }
    entitylist.push(p)
    //Creates Player

    player.setCollideWorldBounds(false);
    //Phaser function so that player cannot bypass world bounds

    this.input.on('pointerdown', function (pointer) {
        //Only shoots if the player has not fired in the past 20 frames.
        if (fire > 20) {
            fire = 0

            var bullet = playerProjectiles.get().setActive(true).setVisible(true);

            if (bullet) {
                bullet.shoot(player, pointer);
                //Projectile moves from shooter(player) to pointer(cursor).
                //this.physics.add.collider(enemy1, bullet, enemyHitFunction);
                var cursorx = pointer.x
                var cursory = pointer.y
                var angle = Math.atan( (cursorx - (config['width'] / 2)) / (cursory - (config['height'] / 2)));
                var shootvariables = {
                    'mousex' : 0,
                    'mousey' : 0,
                    'angle' : angle,
                    'camwidth' : config['width'] / 2,
                    'camheigh' : config['height'] / 2
                }
                var shootjson = JSON.stringify(shootvariables)

            }
        }
    }, this); //Shoots a bullet when the mouse is clicked.


    enemy1 = this.physics.add.sprite(100, 100, 'Enemy');
    enemy1.name = "enemy"
    var e = {
        "username": "enemy",
        "x": enemy1.x,
        "y": enemy1.y,
        "sizeX": 16,
        "sizeY": 16,
        "health": 5
    }
    entitylist.push(e)
    //Creates an enemy
    enemy1.setCollideWorldBounds(true);
    player.setCollideWorldBounds(true);

    // Set object variables

    player.health = 5;
    enemy1.health = 5;

    //Scoreboard stuff. Should change to work with server and backend and stuff.

    //scoreText = this.add.text(16, 64, 'Kills: 0', { fontSize: '32px', fill: '#000' });
    //hpText = this.add.text(16, 16, 'Health: 5', { fontSize: '32px', fill: '#000' });
    //deathText = this.add.text(16, 116, 'Deaths: 0', { fontSize: '32px', fill: '#000' });
    //board = this.add.text(550, 16, 'Top Players:', { fontSize: '32px', fill: '#000' });
    //board1 = this.add.text(550, 66, name + ": 0", { fontSize: '32px', fill: '#000' });
    //board2 = this.add.text(550, 116, 'Enemy: 0', { fontSize: '32px', fill: '#000' });
    document.getElementById("score").innerHTML = "<h3>Enemy: 0</h3><h3>" + name + ": 0</h3>";
    this.cameras.main.startFollow(player, true);
    this.cameras.main.setDeadzone(0, 0);
    this.cameras.main.setZoom(1);
    this.cameras.main.width = 800
    this.cameras.main.height = 800

}

function update(time, delta){


    var cam = this.cameras.main;

    //It's the update function.

    var cursors = this.input.keyboard.addKeys(
            {up: Phaser.Input.Keyboard.KeyCodes.W,
            down: Phaser.Input.Keyboard.KeyCodes.S,
            left: Phaser.Input.Keyboard.KeyCodes.A,
            right: Phaser.Input.Keyboard.KeyCodes.D,
            enemy: Phaser.Input.Keyboard.KeyCodes.O,
                u: Phaser.Input.Keyboard.KeyCodes.I,
                d: Phaser.Input.Keyboard.KeyCodes.K,
                l: Phaser.Input.Keyboard.KeyCodes.J,
                r: Phaser.Input.Keyboard.KeyCodes.L
            });
    if (movecam)
    {
        if (cursors.left.isDown)
        {
            cam.scrollX -= 4;
        }
        else if (cursors.right.isDown)
        {
            cam.scrollX += 4;
        }

        if (cursors.up.isDown)
        {
            cam.scrollY -= 4;
        }
        else if (cursors.down.isDown)
        {
            cam.scrollY += 4;
        }
    }
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
    else if (cursors.down.isDown) {jason['vertical'] = 1;}
    else {jason['vertical'] = 0;}
    if (cursors.up.isDown && cursors.down.isDown) {jason['vertical'] = 0;}
    if (cursors.left.isDown && cursors.right.isDown) {jason['horizontal'] = 0;}
    if (cursors.enemy.isDown) {
        if (efire > 20) {
            efire = 0
            var bullet = playerProjectiles.get().setActive(true).setVisible(true);
            if (bullet) {
                bullet.shoot(enemy1, player);
            }
        }
    }
    if (cursors.u.isDown) {
        enemy1.y -= 4;
    }
    if (cursors.r.isDown) {
        enemy1.x += 4;
    }
    if (cursors.l.isDown){
        enemy1.x -= 4;
    }
    if (cursors.d.isDown) {
        enemy1.y += 4;
    }

    entitylist[0].x = player.x
    entitylist[0].y = player.y
    entitylist[1].x = enemy1.x
    entitylist[1].y = enemy1.y

    for (var q = 0; q < bulletlist.length; q=q+1){
        for (var w = 0; w < entitylist.length; w=w+1){
            if (hitDetection(bulletlist[q], entitylist[w]) == true && bulletlist[q].username != entitylist[w].username){
                bulletlist[q].alive = 0
                entitylist[w].health -= 1
            }
        }
    }
    player.health = entitylist[0].health
    //hpText.setText("Health: " + player.health)
    document.getElementById("myscore").innerHTML = "<h3>Health:" + player.health + "</h3><h3>Kills: " + kills+ "</h3><h3>Deaths: " + deaths + "</h3>";
    enemy1.health = entitylist[1].health

    if (player.health <= 0){
        entitylist[0].health = 5
        player.x = Phaser.Math.Between(50, 750)
        player.y = Phaser.Math.Between(50, 750)
        deaths += 1
        //deathText.setText("Deaths: " + deaths)
        document.getElementById("myscore").innerHTML = "<h3>Health:" + player.health + "</h3><h3>Kills: " + kills+ "</h3><h3>Deaths: " + deaths + "</h3>";
        if (deaths >= kills){
            // scoreHTML = "<h3>Enemy: "+ deaths +"</h3><h3>" + name + ": " + kills + "</h3>";
            //scoreHTML = "something happened";
            document.getElementById("score").innerHTML = "<h3>Enemy: "+ deaths +"</h3><h3>" + name + ": " + kills + "</h3>";
            //board1.setText("Enemy: " + deaths)
            //board2.setText(name + ": " + kills)
        }
        else {
            // scoreHTML = "<h3>" + name + ": " + kills + "</h3><h3>Enemy: "+ deaths +"</h3>";
            // scoreHTML = "something happened";
            document.getElementById("score").innerHTML = "<h3>" + name + ": " + kills + "</h3><h3>Enemy: "+ deaths +"</h3>";
            //board1.setText(name + ": " + kills)
            //board2.setText("Enemy: " + deaths)
        }
    }
    if (enemy1.health <= 0){
        entitylist[1].health = 5
        enemy1.x = Phaser.Math.Between(50, 750)
        enemy1.y = Phaser.Math.Between(50, 750)
        kills += 1
        //scoreText.setText("Kills: " + kills)
        document.getElementById("myscore").innerHTML = "<h3>Health:" + player.health + "</h3><h3>Kills: " + kills+ "</h3><h3>Deaths: " + deaths + "</h3>";
        if (kills >= deaths){
            // scoreHTML = "something happened";
            document.getElementById("score").innerHTML = "<h3>" + name + ": " + kills + "</h3><h3>Enemy: "+ deaths +"</h3>";
            //board1.setText(name + ": " + kills)
            //board2.setText("Enemy: " + deaths)
        }
    else {
            // scoreHTML = "<h3>Enemy: "+ deaths +"</h3><h3>" + name + ": " + kills + "</h3>";
            // scoreHTML = "something happened";
            document.getElementById("score").innerHTML = "<h3>Enemy: "+ deaths +"</h3><h3>" + name + ": " + kills + "</h3>";
            //board1.setText("Enemy: " + deaths)
            //board2.setText(name + ": " + kills)
        }
    }


    // Above code sets variable "horizontal" and "vertical" in dictionary to 1 or 0 based on arrow key inputs
    var jay = JSON.stringify(jason);
    var par = JSON.parse(jay);
    player.x = player.x + (par['horizontal'] * 4);
    player.y = player.y + (par['vertical'] * 4);
    //Sets position for X and Y based on JSON, which is based on arrow key input.
    //Could be set to do velocity instead, but then players would be able to set own position in console
    //Using position instead of velocity the player can sort of bypass world bounds up to its center.
    fire +=1
    efire += 1

    //scoreText.setPosition(player.x - 384,player.y - 336);
    //hpText.setPosition(player.x - 384,player.y - 384);
    //deathText.setPosition(player.x - 384,player.y - 284);
    //board.setPosition(player.x + 150,player.y - 384);
    //board1.setPosition(player.x + 150,player.y - 336);
    //board2.setPosition(player.x + 150,player.y - 284);

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
            this.sizeX = 16
            this.sizeY = 16
            this.a = 0
        },
    //Fire function for projectile. Moves from object shooting to target
    shoot: function (int, dest)
    {
        // Initial position is set to the object shooting's position
        this.x = int.x
        this.y = int.y
        this.angle = Math.atan( (dest.x-(config['width'] / 2)) / (dest.y-(config['height'] / 2)));
        if (int.name == "enemy"){
            this.angle = Math.atan( (dest.x-int.x) / (dest.y-int.y));
        }
        // Calculate the xSpeed and ySpeed of projectile to moves it from initial position to target
        this.xSpeed = this.speed*Math.sin(this.angle);
        this.ySpeed = this.speed*Math.cos(this.angle);
        if (int.name != "enemy" && dest.y < config['height'] / 2) {
                this.xSpeed = -this.xSpeed
                this.ySpeed = -this.ySpeed
            }
        if (int.name == "enemy" && dest.y < this.y){
            this.xSpeed = -this.xSpeed
            this.ySpeed = -this.ySpeed
        }
        this.age = 0; // Age of projectile
        shotid +=1
        this.a = shotid
        var identity = {
            "username": int.name,
            "id": shotid,
            "x": this.x,
            "y": this.y,
            "sizeX": this.sizeX,
            "sizeY": this.sizeY,
            "alive": 1
        }
        bulletlist.push(identity)
    },
    // Updates the position and age of the projectile each cycle
    update: function () {
        this.age += 1;
        if (this.age > 120) {
            del(this)
            //projectile destroys intself after 120 frames
        }
        this.x += this.xSpeed;
        this.y += this.ySpeed;
        for (var i = 0; i < bulletlist.length; i=i+1){
            if (this.a == bulletlist[i]["id"]){
                if (bulletlist[i].alive == 0){
                    del(this)
                }
                else {
                    bulletlist[i].x = this.x
                    bulletlist[i].y = this.y
                }
            }
        }
    }
});