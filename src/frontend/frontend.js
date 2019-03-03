function startGame() {
    new Phaser.Game(config);
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
            playerBullets: null,
            enemyBullets: null,
            time: 0,
        }
    }
};

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


function preload(){
    this.load.image('Player', 'assets/player.png');
    this.load.image('Shot', 'assets/shot.png');
    this.load.image('Enemy', 'assets/enemy.png');
    this.load.image('EnemyBullet', 'assets/bullet.png');
    this.load.image('Background', 'assets/bg.png');
}

function create () {

    playerBullets = this.physics.add.group({ classType: Bullet, runChildUpdate: true });
    enemyBullets = this.physics.add.group({ classType: Bullet, runChildUpdate: true });


    this.add.image(400, 400, 'Background');
    player = this.physics.add.sprite(400, 600, 'Player');
    player.setCollideWorldBounds(true);

    this.input.on('pointerdown', function (pointer, time, lastFired) {
        if (fire > 20) {
            fire = 0
            if (player.active === false)
                return;

            var bullet = playerBullets.get().setActive(true).setVisible(true);

            if (bullet) {
                bullet.fire(player, pointer);
                this.physics.add.collider(enemy1, bullet, enemyHitCallback);
            }
        }
    }, this);

    enemy1 = this.physics.add.sprite(100, 100, 'Enemy');

    enemy1.setCollideWorldBounds(true);
    enemy1.setVelocity(Phaser.Math.Between(80, 200), Phaser.Math.Between(80, 200));
    enemy1.setBounce(1)

    // Set sprite variables
    player.health = 5;
    enemy1.health = 5;
    enemy1.lastFired = 0;
    scoreText = this.add.text(16, 66, 'Kills: 0', { fontSize: '32px', fill: '#000' });
    hpText = this.add.text(16, 16, 'Health: 5', { fontSize: '32px', fill: '#000' });
    deathText = this.add.text(16, 116, 'Deaths: 0', { fontSize: '32px', fill: '#000' });
    board = this.add.text(550, 16, 'Top Players:', { fontSize: '32px', fill: '#000' });
    board1 = this.add.text(550, 66, 'Player: 0', { fontSize: '32px', fill: '#000' });
    board2 = this.add.text(550, 116, 'Enemy: 0', { fontSize: '32px', fill: '#000' });




}

function update(time, delta){
    var cursors = this.input.keyboard.createCursorKeys();
    if (cursors.left.isDown) {player.setVelocityX(-200);}
    else if (cursors.right.isDown) {player.setVelocityX(200);}
    else {player.setVelocityX(0);}
    if (cursors.up.isDown) {player.setVelocityY(-200);}
    else if (cursors.down.isDown) {player.setVelocityY(200);}
    else {player.setVelocityY(0);}
    if (cursors.up.isDown && cursors.down.isDown) {player.setVelocityY(0);}
    if (cursors.left.isDown && cursors.right.isDown) {player.setVelocityX(0);}
    enemyFire(enemy1, player, time, this);
    fire +=1

    return;
}




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
        }
    }

});

function enemyHitCallback(enemyHit, bulletHit)
{
    // Reduce health of enemy
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
        }

        // Destroy bullet
        bulletHit.setActive(false).setVisible(false);
    }
}

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

        }

        // Destroy bullet
        bulletHit.setActive(false).setVisible(false);
    }
}

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