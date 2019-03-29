function startGame() {
    new Phaser.Game(config);
} //Phaser startgame

var name = prompt("Please enter your name", "Username");

var jason = {
    'username' : name,
    'vertical' : 0,
    'horizontal' : 0
} //Initializes the dictionary that will be used to send JSON to server.


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
var imm = -300
//More Phaser Stuff and variables


function preload(){
    this.load.image('Player', 'assets/player.png');
    this.load.image('Shot', 'assets/shot.png');
    this.load.image('Enemy', 'assets/enemy.png');
    this.load.image('EnemyProjectile', 'assets/bullet.png');
    this.load.image('Background', 'assets/bg.png');
}//More Phaser Stuff PT. 3. Loads images for sprites

function create () {



    playerProjectiles = this.physics.add.group({ classType: Projectile, runChildUpdate: true });
    enemyProjectiles = this.physics.add.group({ classType: Projectile, runChildUpdate: true });
    //Creates classes for player projectiles and enemy projectiles, so that they don't shoot themselves. Unsure of how this might work in server, but probably easily doable.


    this.add.image(400, 400, 'Background');
    //Background.jpg

    player = this.physics.add.sprite(400, 600, 'Player');
    //Creates Player

    player.setCollideWorldBounds(true);
    //Phaser function so that player cannot bypass world bounds

    this.input.on('pointerdown', function (pointer, time, lastFired) {
        //Only shoots if the player has not fired a shot in the past 20 frames
         if (fire > 20) {
            fire = 0
            if (player.active === false)
                return;

            var projectile = playerProjectiles.get().setActive(true).setVisible(true);

            if (projectile) {
                projectile.shoot(player, pointer);
                //Projectile moves from shooter(player) to pointer(cursor).
                this.physics.add.collider(enemy1, projectile, enemyHitFunction);
            }
        }

    }, this); //Shoots a projectile when the mouse is clicked.

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
    scoreText = this.add.text(16, 66, 'Kills: 0', { fontSize: '32px', fill: '#000' });
    hpText = this.add.text(16, 16, 'Health: 5', { fontSize: '32px', fill: '#000' });
    deathText = this.add.text(16, 116, 'Deaths: 0', { fontSize: '32px', fill: '#000' });
    board = this.add.text(550, 16, 'Top Players:', { fontSize: '32px', fill: '#000' });
    board1 = this.add.text(550, 66, name + ": 0", { fontSize: '32px', fill: '#000' });
    board2 = this.add.text(550, 116, 'Enemy: 0', { fontSize: '32px', fill: '#000' });




}

function update(time){

    //It's the update function.

    var cursors = this.input.keyboard.createCursorKeys();
    if (cursors.left.isDown) {jason['horizontal'] = -1;}
    else if (cursors.right.isDown) {jason['horizontal'] = 1;}
    else {jason['horizontal'] = 0;}
    if (cursors.up.isDown) {jason['vertical'] = -1;}
    else if (cursors.down.isDown) {jason['vertical'] = 1;}
    else {jason['vertical'] = 0;}
    if (cursors.up.isDown && cursors.down.isDown) {jason['vertical'] = 0;}
    if (cursors.left.isDown && cursors.right.isDown) {jason['horizontal'] = 0;}
    // Above code sets variable "horizontal" and "vertical" in dictionary to 1 or 0 based on arrow key inputs



    var jay = JSON.stringify(jason);
    var par = JSON.parse(jay);

    player.x = player.x + (par['horizontal'] * 4);
    player.y = player.y + (par['vertical'] * 4);
    //Sets position for X and Y based on JSON, which is based on arrow key input.
    //Could be set to do velocity instead, but then players would be able to set own position in console
    //Using position instead of velocity the player can sort of bypass world bounds up to its center.
    //Json (jay) = 'username': player's username 'horizontal' : 0 or 1, 'vertical' : 0 or 1


    enemyFire(enemy1, player, time, this);
    //Enemy shoots a projectile whenever it's cooldown is over.

    fire +=1


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
    shoot: function (int, dest)
    {
        // Initial position is set to the object shooting's position
        this.x = int.x
        this.y = int.y

        this.angle = Math.atan( (dest.x-this.x) / (dest.y-this.y));

        // Calculate the xSpeed and ySpeed of projectile to moves it from initial position to target
        this.xSpeed = this.speed*Math.sin(this.angle);
        this.ySpeed = this.speed*Math.cos(this.angle);

        if (dest.y < this.y)
        {
            this.xSpeed = -this.xSpeed
            this.ySpeed = -this.ySpeed
        }

        this.age = 0; // Age of projectile
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