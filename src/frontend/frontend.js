function startGame() {
    new Phaser.Game(config);
} //Phaser startgame

var jason = {
    'vertical' : 0,
    'horizontal' : 0
} //Initializes the dictionary that will be used to send JSON to server.

var config = {
    "type" : Phaser.AUTO,
    "width" : 1600,
    "height" : 1600,
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
            playerBullets: null,
            enemyBullets: null,
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
//More Phaser Stuff


function preload(){
    this.load.image('Player', 'assets/player.png');
    this.load.image('Shot', 'assets/shot.png');
    this.load.image('Enemy', 'assets/enemy.png');
    this.load.image('EnemyBullet', 'assets/bullet.png');
    this.load.image('Background', 'assets/bg.png');
}//More Phaser Stuff PT. 3

function create () {

    playerBullets = this.physics.add.group({ classType: Bullet, runChildUpdate: true });
    enemyBullets = this.physics.add.group({ classType: Bullet, runChildUpdate: true });
    //Creates classes for player bullets and enemy bullets, so that they don't shoot themselves. Unsure of how this might work in server, but probably easily doable.

    for (y = 0; y < 2; y++)
    {
        for (x = 0; x < 2; x++)
        {
            this.add.image(400 * x, 400 * y, 'Background').setOrigin(0).setAlpha(0.75);
        }
    }
    //this.add.image(400, 400, 'Background');
    //Background.jpg

    player = this.physics.add.sprite(400, 400, 'Player');
    //Creates Player

    player.setCollideWorldBounds(true);
    //Phaser function so that player cannot bypass world bounds

    this.input.on('pointerdown', function (pointer, time, lastFired) {
        //Only shoots if the player has not fired in the past 20 frames.
        if (fire > 20) {
            fire = 0
            if (player.active === false)
                return;

            var bullet = playerBullets.get().setActive(true).setVisible(true);

            if (bullet) {
                bullet.fire(player, pointer);
                //Bullet moves from shooter(player) to pointer(cursor).
                this.physics.add.collider(enemy1, bullet, enemyHitCallback);
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
    scoreText = this.add.text(16, 66, 'Kills: 0', { fontSize: '32px', fill: '#000' });
    hpText = this.add.text(16, 16, 'Health: 5', { fontSize: '32px', fill: '#000' });
    deathText = this.add.text(16, 116, 'Deaths: 0', { fontSize: '32px', fill: '#000' });
    board = this.add.text(550, 16, 'Top Players:', { fontSize: '32px', fill: '#000' });
    board1 = this.add.text(550, 66, 'Player: 0', { fontSize: '32px', fill: '#000' });
    board2 = this.add.text(550, 116, 'Enemy: 0', { fontSize: '32px', fill: '#000' });

    this.cameras.main.startFollow(player, true);
    this.cameras.main.setDeadzone(100, 50);
    this.cameras.main.setZoom(0.5);
    this.input.on('pointerdown', function () {
        moveCam = (moveCam) ? false: true;
    });


}

function update(time, delta){
    var cam = this.cameras.main;

    //It's the update function.

    var cursors = this.input.keyboard.createCursorKeys();
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
    // Above code sets variable "horizontal" and "vertical" in dictionary to 1 or 0 based on arrow key inputs

    var jay = JSON.stringify(jason);
    var par = JSON.parse(jay);

    player.x = player.x + (par['horizontal'] * 4);
    player.y = player.y + (par['vertical'] * 4);
    //Sets position for X and Y based on JSON, which is based on arrow key input.
    //Could be set to do velocity instead, but then players would be able to set own position in console
    //Using position instead of velocity the player can sort of bypass world bounds up to its center.


    enemyFire(enemy1, player, time, this);
    //Enemy shoots a bullet whenever it's cooldown is over.

    fire +=1

    return;
}



//Class for bullets. Handles how they act.
var Bullet = new Phaser.Class({

    Extends: Phaser.GameObjects.Image,

    initialize:

    // Bullet Constructor
        function Bullet (scene)
        {
            Phaser.GameObjects.Image.call(this, scene, 0, 0, 'Shot');
            this.speed = .5;
            this.born = 0;
            this.direction = 0;
            this.xSpeed = 0;
            this.ySpeed = 0;
            this.setSize(15, 15, true);
        },

    // Fires a bullet from the player to the reticle
    fire: function (shooter, target)
    {
        this.setPosition(shooter.x, shooter.y); // Initial position
        this.direction = Math.atan( (target.x-this.x) / (target.y-this.y));

        // Calculate X and y velocity of bullet to moves it from shooter to target
        if (target.y >= this.y)
        {
            this.xSpeed = this.speed*Math.sin(this.direction);
            this.ySpeed = this.speed*Math.cos(this.direction);
        }
        else
        {
            this.xSpeed = -this.speed*Math.sin(this.direction);
            this.ySpeed = -this.speed*Math.cos(this.direction);
        }

        this.rotation = shooter.rotation; // angle bullet with shooters rotation
        this.born = 0; // Time since new bullet spawned
    },

    // Updates the position of the bullet each cycle
    update: function (time, delta) {
        this.x += this.xSpeed * delta;
        this.y += this.ySpeed * delta;
        this.born += delta;
        if (this.born > 1800) {
            this.setActive(false);
            this.setVisible(false);
            //bullet hides and deactivates itself after 1800 frames
        }
    }

});


//Example collision code for demonstration.
function enemyHitCallback(enemyHit, bulletHit)
{
    // Randomize speed and direction after getting shot
    enemyHit.setVelocity(Phaser.Math.Between(80, 200), Phaser.Math.Between(80, 200));
    if (bulletHit.active === true && enemyHit.active === true)
    {
        enemyHit.health = enemyHit.health - 1;
        console.log("Enemy hp: ", enemyHit.health);

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
                board1.setText("Player: " + kills)
            }
            if (deaths > kills){
                board1.setText("Enemy: " + deaths)
                board2.setText("Player: " + kills)
            }
        }

        // Destroy bullet
        bulletHit.setActive(false).setVisible(false);
    }
}

//Example collision code for demonstration.
function playerHitCallback(playerHit, bulletHit)
{
    // Reduce health of player
    if (bulletHit.active === true && playerHit.active === true)
    {
        playerHit.health = playerHit.health - 1;
        console.log("Player hp: ", playerHit.health);
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
                board2.setText("Player: " + kills)
            }
            if (kills > deaths){
                board2.setText("Enemy: " + deaths)
                board1.setText("Player: " + kills)
            }

        }

        // Destroy bullet
        bulletHit.setActive(false).setVisible(false);
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

        // Get bullet from bullets group
        var bullet = enemyBullets.get().setActive(true).setVisible(true);

        if (bullet)
        {
            bullet.fire(enemy1, player);
            // Add collider between bullet and player
            gameObject.physics.add.collider(player, bullet, playerHitCallback);
        }
    }
}