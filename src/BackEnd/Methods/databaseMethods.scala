package BackEnd.Methods

import java.sql.{Connection, DriverManager, ResultSet}

import BackEnd.Methods.password.word

object databaseMethods {
  val url = "jdbc:mysql://localhost/mysql?serverTimezone=UTC"
  val username = "root"
  val pass: String = word

  var connection: Connection = DriverManager.getConnection(url, username, pass)


  var id: Int = 1

  /**
    * format of the table:
    *
    * player table:
    * name, x, y, kills, deaths, angle(may be null),
    *
    * projX(may be null), projY(may be null), startX(may be null), startY(may be null)
    *
    * projectile table:
    * user, x, y, angle, startingX?, startingY?
    */


  def setTable(): Unit = {
    val statement = connection.createStatement()

    // Don't do this in a real app. If the table exists all your data will be deleted.
    // We're doing this in testing to avoid duplicate entries
    //statement.execute("DROP TABLE IF EXISTS players")
    //statement.execute("CREATE TABLE players (username TEXT, points INT, locationX DOUBLE, locationY Double)")

    // Use in real app to avoid deleting your table
    statement.execute("CREATE TABLE IF NOT EXISTS Players (name TEXT, x DOUBLE, y DOUBLE, kills INT, deaths INT, angle DOUBLE, lastUpdate Long)")
    statement.execute("CREATE TABLE IF NOT EXISTS Projectiles (user TEXT, id INT, x DOUBLE, y DOUBLE, lastUpdate Long, lifetime Long)")
  }

  def playerExists(name: String): Boolean = {
    val statement = connection.prepareStatement("SELECT * FROM Players WHERE name=?")
    statement.setString(1, name)
    val result: ResultSet = statement.executeQuery()
    result.next()
  }


  def makePlayer(name: String): Unit = {
    val statement = connection.prepareStatement("INSERT INTO Players VALUE (?, ?, ?, ?, ?, ?)")
    statement.setString(1, name) // name
    statement.setDouble(2, math.random()*800)  // x
    statement.setDouble(3, math.random()*800)  // y
    statement.setInt(4, 0)       // kills
    statement.setInt(5, 0)       // deaths
    // statement.setDouble(6, null)  // angle  // I DON'T THINK PLAYERS WILL FIRE THE SECOND THEY SPAWN IN THE GAME
    statement.setLong(6, System.nanoTime())
    statement.execute()
  }

  def makeProjectile(name: String, angle: Double): Int = { // it only returns an int so that gameActor can send new projectiles
    var statement = connection.prepareStatement("SELECT * FROM Players WHERE name=?")
    statement.setString(1, name)
    val result: ResultSet = statement.executeQuery()
    result.next()
    val x: Double = result.getDouble("x")
    val y: Double = result.getDouble("y")

    statement = connection.prepareStatement("INSERT INTO Projectiles VALUE (?, ?, ?, ?, ?, ?, ?)")

    statement.setString(1, name)
    statement.setInt(2, id+1)
    statement.setDouble(3, x)
    statement.setDouble(4, y)
    statement.setDouble(5, angle)
    statement.setLong(6, System.nanoTime())
    statement.setLong(7, (0.0).toLong)
    statement.execute()
    id+=1
    id

  }


  def updatePlayerPos(name: String, vert: Int, horiz: Int): Unit = {
    val speed: Double = 50.0 // 50 px / second

    var statement = connection.prepareStatement("SELECT * FROM Players WHERE name=?")
    statement.setString(1, name)
    val result: ResultSet = statement.executeQuery()
    result.next()
    val x: Double = result.getDouble("x")
    val y: Double = result.getDouble("y")
    val time: Long = result.getLong("lastUpdate")
    val deltaS = (System.nanoTime() - time) / 1000000000.0

    statement = connection.prepareStatement("UPDATE Players SET x=?, y=?, lastUpdate=? WHERE name=?")
    statement.setDouble(1, x+(horiz*speed*deltaS))
    statement.setDouble(2, y+(vert*speed*deltaS))
    statement.setLong(3, time)
    statement.setString(4, name)

    statement.execute()
  }


  def updateProjectilePos(id: Int): Unit = {
    val speed: Double = 60.0 // 60 px / second

    var statement = connection.prepareStatement("SELECT * FROM Projectiles WHERE id=?")
    statement.setInt(1, id)
    val result: ResultSet = statement.executeQuery()
    result.next()

    val x: Double = result.getDouble("x")
    val y: Double = result.getDouble("y")
    val angle: Double = result.getDouble("angle")

    val time: Long = result.getLong("lastUpdate")
    val deltaS: Long = System.nanoTime() - time

    statement = connection.prepareStatement("UPDATE Projectiles SET x=?, y=?, lastUpdate=?, lifetime=? WHERE id=?")
    // this might be a problem?
    statement.setDouble(1, x+(math.cos(angle)*speed*deltaS))
    statement.setDouble(2, y+(math.sin(angle)*speed*deltaS))
    statement.setLong(3, time)  // System.nanoTime()?
    statement.setLong(4, time + deltaS)  // could change?
    statement.execute()
  }




  def collision(name: String, id: Int): Unit ={
    // reset position of name
    // increment deaths of name
    var statement = connection.prepareStatement("SELECT * FROM Players WHERE name=?")
    statement.setString(1, name)
    val result: ResultSet = statement.executeQuery()
    result.next()
    val deaths: Int = result.getInt("deaths")

    statement = connection.prepareStatement("UPDATE Players SET x=?, y=?, deaths=? WHERE name=?")
    statement.setDouble(1, math.random()*800)
    statement.setDouble(2, math.random()*800)
    statement.setInt(3, deaths+1)
    statement.setString(4, name)
    statement.execute()


    // increment kills of user
    // delete projectile(row)
    statement = connection.prepareStatement("SELECT * FROM Projectiles WHERE id=?")
    statement.setInt(1, id)
    val resultTwo: ResultSet = statement.executeQuery()
    resultTwo.next()
    val user: String = resultTwo.getString("user")

    statement = connection.prepareStatement("DELETE FROM Projectiles WHERE id=?")
    statement.setInt(1, id)
    statement.execute()

    statement = connection.prepareStatement("SELECT * FROM Players WHERE name=?")
    statement.setString(1, user)
    val resultThree: ResultSet = statement.executeQuery()
    resultThree.next()
    val kills: Int = resultThree.getInt("kills")

    statement = connection.prepareStatement("UPDATE Players SET kills=? WHERE name=?")
    statement.setInt(1, kills+1)
    statement.setString(2, user)
    statement.execute()
  }

  def checkCollision(): Map[Int, String] = { // this will return a map of id's paired with names

    var theMap: Map[Int, String] = Map()
    val playerRadius: Double = 16.0
    // for each projectile, check each player to see if it is within it's size
    val projectiles: ResultSet = connection.prepareStatement("SELECT * FROM Projectiles").executeQuery()
    val players: ResultSet = connection.prepareStatement("SELECT * FROM Players").executeQuery()

    val projectileMap: Map[Int, List[String]] = Map()
    while(projectiles.next()) {
      val projUser: String = projectiles.getString("user")
      val projX: String = projectiles.getDouble("x").toString
      val projY: String = projectiles.getDouble("y").toString
      val id: Int = projectiles.getInt("id")
      projectileMap(id) -> List(projX, projY, projUser)
    }

    val playerMap: Map[String, List[Double]] = Map()
    while(players.next()){
      val x: Double = players.getDouble("x")
      val y: Double = players.getDouble("y")
      val theName: String = players.getString("name")
      playerMap(theName) -> List(x, y)
    }

    for (projKey <- projectileMap.keys){
      for (playKey <- playerMap.keys){
        // distance formula
        val xSquared: Double = math.pow(projectileMap(projKey)(0).toDouble - playerMap(playKey)(0), 2)
        val ySquared: Double = math.pow(projectileMap(projKey)(1).toDouble - playerMap(playKey)(1), 2)
        val d: Double = math.pow(xSquared + ySquared, 0.5)
        if (d < playerRadius && playKey != projectileMap(projKey)(3)){
          theMap(projKey) -> playKey
        }
      }
    }
    theMap
  }

  def deletePlayer(name: String): Unit = {
    // delete player from the table
    var statement = connection.prepareStatement("DELETE FROM Players WHERE name=?")
    statement.setString(1, name)
    statement.execute()

    // delete their projectiles from the table
    statement = connection.prepareStatement("DELETE FROM Projectiles WHERE user=?")
    statement.setString(1, name)
    statement.execute()
  }


  def CheckOutOfBounds(name: String): Unit = {
    val worldSize: Double = 800.0
    val statement = connection.prepareStatement("SELECT * FROM Players WHERE name=?")
    statement.setString(1, name)
    val result: ResultSet = statement.executeQuery()
    result.next()
    val playerX: Double = result.getDouble("x")
    val playerY: Double = result.getDouble("y")
    if(playerX > worldSize){
      // if player is beyond 800
      val theStatement = connection.prepareStatement("UPDATE Players SET x=? WHERE name=?")
      theStatement.setDouble(1, worldSize)
      theStatement.setString(2, name)
      statement.execute()
    }else if(playerX < worldSize){
      val theStatement = connection.prepareStatement("UPDATE Players SET x=? WHERE name=?")
      theStatement.setDouble(1, 0.0)
      theStatement.setString(2, name)
      statement.execute()
    }
    if(playerY > worldSize){
      val theStatement = connection.prepareStatement("UPDATE Players SET y=? WHERE name=?")
      theStatement.setDouble(1, worldSize)
      theStatement.setString(2, name)
      statement.execute()
    }else if(playerY < worldSize){
      val theStatement = connection.prepareStatement("UPDATE Players SET y=? WHERE name=?")
      theStatement.setDouble(1, 0.0)
      theStatement.setString(2, name)
      statement.execute()
    }
  }


  def checkLifeTime(id: Int): Unit = {
    val lifeTime: Double = 2.0 // seconds
    val statement = connection.prepareStatement("SELECT * FROM Projectiles WHERE id=?")
    statement.setInt(1, id)
    val result: ResultSet = statement.executeQuery()
    result.next()
    val currentLife: Long = result.getLong("lastUpdate")
    if ((System.nanoTime()-currentLife)/1000000000.0 > lifeTime){
      val statementTwo = connection.prepareStatement("DELETE FROM Projectiles WHERE id=?")
      statementTwo.setInt(1, id)
      statement.execute()
    }
  }


  def getPlayerProjectiles(name: String): List[Map[String, Double]] = {
    var theList: List[Map[String, Double]] = List()
    val statement = connection.prepareStatement("SELECT * FROM Projectiles WHERE user=?")
    statement.setString(1, name)
    val result: ResultSet = statement.executeQuery()
    while(result.next()){
      var thisMap: Map[String, Double] = Map()
      val x: Double = result.getDouble("x")
      val y: Double = result.getDouble("y")
      thisMap = Map("x"-> x, "y"-> y)
      theList=theList:+thisMap
    }
    theList
  }


  def getAllPlayers(): List[Map[String, String]] = {
    // fix this to add projectiles and whatnot
    var theList: List[Map[String, String]] = List()
    // var theMap: Map[String, String] = Map()
    val result: ResultSet = connection.prepareStatement("SELECT * FROM Players").executeQuery()
    while (result.next()){
      val name: String = result.getString("name")
      val x: String = result.getDouble("x").toString
      val y: String = result.getDouble("y").toString
      val kills: String = result.getInt("kills").toString
      // val deaths: String = result.getInt("deaths").toString
      // val projectiles: List[Map[String, Double]] = getPlayerProjectiles(name)
      theList=theList:+Map(
        "Name" -> name,
        "x" -> x,
        "y" -> y,
        "Kills" -> kills,
        // "Projectile" -> projectiles
        // "Deaths" -> deaths
      )
    }
    theList
  }

  // def getNewProjectiles():


}
