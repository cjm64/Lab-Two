package BackEnd.Methods

import java.sql.{Connection, DriverManager, ResultSet}

import BackEnd.Methods.password.word

object databaseMethods {

  var players: Map[String, Map[String, String]] = Map()
  var projectiles: List[Map[String, String]] = List()


  val url = "jdbc:mysql://localhost/mysql?serverTimezone=UTC"
  val username = "root"
  val pass: String = word

  var connection: Connection = DriverManager.getConnection(url, username, pass)

  var Projectiles: List[Map[String, String]] = List()
  var Players: Map[String, Map[String, String]] = Map()

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

    statement.execute("DROP TABLE IF EXISTS Player")
    statement.execute("DROP TABLE IF EXISTS Projectiles")
    // Use in real app to avoid deleting your table
    statement.execute("CREATE TABLE IF NOT EXISTS Projectiles (user TEXT, id INT, x DOUBLE, y DOUBLE, angle DOUBLE, lastUpdate Long, lifetime Double)")
    statement.execute("CREATE TABLE IF NOT EXISTS Player (x DOUBLE, y DOUBLE, kills INT, deaths INT, lastUpdate Long, theName TEXT, inputX INT, inputY INT)")
    // statement.execute("CREATE TABLE IF NOT EXISTS Projectiles (user TEXT, id INT, x DOUBLE, y DOUBLE, lastUpdate Long, lifetime Long)")
  }

/*
  def playerExists(name: String): Boolean = {
    val statement = connection.prepareStatement("SELECT * FROM Player WHERE theName=?")
    // val statement = connection.prepareStatement("SELECT * FROM Players")
    statement.setString(1, name)
    val result: ResultSet = statement.executeQuery()
    // println(result)
    result.next()
    // result.getString("name")
  }


  def makePlayer(name: String): Unit = {
    val statement = connection.prepareStatement("INSERT INTO Player VALUE (?, ?, ?, ?, ?, ?, ?, ?)")
    // statement.setString(1, name) // name
    statement.setDouble(1, math.random()*800)  // x
    statement.setDouble(2, math.random()*800)  // y
    statement.setInt(3, 0)       // kills
    statement.setInt(4, 0)       // deaths
    // statement.setDouble(6, null)  // angle  // I DON'T THINK PLAYERS WILL FIRE THE SECOND THEY SPAWN IN THE GAME
    statement.setLong(5, System.nanoTime())
    statement.setString(6, name)
    statement.setInt(7, 0)
    statement.setInt(8, 0)
    statement.execute()
  }

  def makeProjectile(name: String, angle: Double): Int = { // it only returns an int so that gameActor can send new projectiles
    var statement = connection.prepareStatement("SELECT * FROM Player WHERE theName=?")
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
    statement.setDouble(7, 0.0)
    statement.execute()
    id+=1
    id

  }


  def updatePlayerPos(name: String): Unit = {
    val speed: Double = 50.0 // 50 px / second

    var statement = connection.prepareStatement("SELECT * FROM Player WHERE theName=?")
    statement.setString(1, name)
    val result: ResultSet = statement.executeQuery()
    result.next()
    val x: Double = result.getDouble("x")
    val y: Double = result.getDouble("y")
    val vert: Int = result.getInt("inputY")
    val horiz: Int = result.getInt("inputX")
    val time: Long = result.getLong("lastUpdate")
    val deltaS = (System.nanoTime() - time) / 1000000000.0

    statement = connection.prepareStatement("UPDATE Player SET x=?, y=?, lastUpdate=? WHERE theName=?")
    statement.setDouble(1, x+(horiz*speed*deltaS))
    statement.setDouble(2, y+(vert*speed*deltaS))
    statement.setLong(3, System.nanoTime())
    statement.setString(4, name)

    statement.execute()
  }


  def updateProjectilePos(id: Int): Unit = {
    val speed: Double = 100.0 // 60 px / second

    var statement = connection.prepareStatement("SELECT * FROM Projectiles WHERE id=?")
    statement.setInt(1, id)
    val result: ResultSet = statement.executeQuery()
    result.next()

    val x: Double = result.getDouble("x")
    val y: Double = result.getDouble("y")
    val angle: Double = result.getDouble("angle")

    val time: Long = result.getLong("lastUpdate")
    val life: Double = result.getDouble("lifetime")
    val deltaS: Double = (System.nanoTime() - time)/1000000000.0

    statement = connection.prepareStatement("UPDATE Projectiles SET x=?, y=?, lastUpdate=?, lifetime=? WHERE id=?")
    // this might be a problem?
    statement.setDouble(1, x+(math.sin(angle)*speed*deltaS))
    statement.setDouble(2, y+(math.cos(angle)*speed*deltaS))
    statement.setLong(3, System.nanoTime())  // System.nanoTime()?
    statement.setDouble(4, life + deltaS)  // could change?
    statement.setInt(5, id)
    statement.execute()
  }




  def collision(name: String, id: Int): Unit ={
    // reset position of name
    // increment deaths of name

    var statement = connection.prepareStatement("UPDATE Player SET x=?, y=? WHERE theName=?")
    statement.setDouble(1, math.random()*800)
    statement.setDouble(2, math.random()*800)
    statement.setString(3, name)
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

    statement = connection.prepareStatement("SELECT * FROM Player WHERE theName=?")
    statement.setString(1, user)
    val resultThree: ResultSet = statement.executeQuery()
    resultThree.next()
    val kills: Int = resultThree.getInt("kills")

    statement = connection.prepareStatement("UPDATE Player SET kills=? WHERE theName=?")
    statement.setInt(1, kills+1)
    statement.setString(2, user)
    statement.execute()
  }

  def checkCollision(): Map[Int, String] = { // this will return a map of id's paired with names

    var theMap: Map[Int, String] = Map()
    val playerRadius: Double = 16.0
    // for each projectile, check each player to see if it is within it's size
    val projectiles: ResultSet = connection.createStatement().executeQuery("SELECT * FROM Projectiles")
    val players: ResultSet = connection.createStatement().executeQuery("SELECT * FROM Player")

    var projectileMap: Map[Int, List[String]] = Map()
    while(projectiles.next()) {
      val projUser: String = projectiles.getString("user")
      val projX: String = projectiles.getDouble("x").toString
      val projY: String = projectiles.getDouble("y").toString
      val id: Int = projectiles.getInt("id")
      // projectileMap(id) -> List(projX, projY, projUser)
      projectileMap += id -> List(projX, projY, projUser)

    }

    var playerMap: Map[String, List[Double]] = Map()
    while(players.next()){
      val x: Double = players.getDouble("x")
      val y: Double = players.getDouble("y")
      val theName: String = players.getString("theName")
      // playerMap(theName) = List(x, y)
      playerMap += theName->List(x, y)
    }

    for (projKey <- projectileMap.keys){
      for (playKey <- playerMap.keys){
        // distance formula
        val xSquared: Double = math.pow(projectileMap(projKey)(0).toDouble - playerMap(playKey)(0), 2)
        val ySquared: Double = math.pow(projectileMap(projKey)(1).toDouble - playerMap(playKey)(1), 2)
        val d: Double = math.pow(xSquared + ySquared, 0.5)
        if (d < playerRadius && playKey != projectileMap(projKey)(2)){
          // theMap(projKey) = playKey
          theMap += projKey -> playKey
        }
      }
    }
    theMap
  }

  def deletePlayer(name: String): Unit = {
    // delete player from the table
    var statement = connection.prepareStatement("DELETE FROM Player WHERE theName=?")
    statement.setString(1, name)
    statement.execute()

    // delete their projectiles from the table
    statement = connection.prepareStatement("DELETE FROM Projectiles WHERE user=?")
    statement.setString(1, name)
    statement.execute()
  }

  def CheckOutOfBounds(name: String): Unit = {
    val worldSize: Double = 800.0
    val result: Map[String, String] = Players(name)
    val playerX: Double = result("x").toDouble
    val playerY: Double = result("y").toDouble
    if(playerX > worldSize){
      // if player is beyond 800
      Players(name)("x") = worldSize.toString
    }else if(playerX < 0.0){
      Players(name)("x") = 0.0.toString
    }
    if(playerY > worldSize){
      Players(name)("y") = worldSize.toString
    }else if(playerY < 0.0){
      Players(name)("y") = 0.0.toString
    }
  }

  def CheckOutOfBoundsDatabase(name: String): Unit = {
    val worldSize: Double = 800.0
    val statement = connection.prepareStatement("SELECT * FROM Player WHERE theName=?")
    statement.setString(1, name)
    val result: ResultSet = statement.executeQuery()
    result.next()
    val playerX: Double = result.getDouble("x")
    val playerY: Double = result.getDouble("y")
    if(playerX > worldSize){
      // if player is beyond 800
      val theStatement = connection.prepareStatement("UPDATE Player SET x=? WHERE theName=?")
      theStatement.setDouble(1, worldSize)
      theStatement.setString(2, name)
      statement.execute()
    }else if(playerX < worldSize){
      val theStatement = connection.prepareStatement("UPDATE Player SET x=? WHERE theName=?")
      theStatement.setDouble(1, 0.0)
      theStatement.setString(2, name)
      statement.execute()
    }
    if(playerY > worldSize){
      val theStatement = connection.prepareStatement("UPDATE Player SET y=? WHERE theName=?")
      theStatement.setDouble(1, worldSize)
      theStatement.setString(2, name)
      statement.execute()
    }else if(playerY < worldSize){
      val theStatement = connection.prepareStatement("UPDATE Player SET y=? WHERE theName=?")
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
    for(i <- Projectiles){
      if(i("id") == id.toString){
        val lastUpdate: Long = i("lastUpdate").toLong
        val currentLife: Double = i("lifetime").toDouble
        val deltaS: Double = (System.nanoTime() - lastUpdate)/1000000000.0
        if (currentLife+deltaS > lifeTime){
          Projectiles = Projectiles.filter(_ != i)
        }
      }
    }

  }

  def checkLifeTimeDatabase(id: Int): Unit = {
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


  def updatePlayerInput(name: String, vert: Int, horiz: Int): Unit = {
    Players(name)("inputX") = horiz.toString
    Players(name)("inputY") = vert.toString
  }

  def updatePlayerInputDatabase(name: String, vert: Int, horiz: Int): Unit = {
    val statement = connection.prepareStatement("UPDATE Player SET inputX=?, inputY=? WHERE theName=?")
    statement.setInt(1, horiz)
    statement.setInt(2, vert)
    statement.setString(3, name)
    statement.execute()
  }


  def getPlayerProjectiles(name: String): List[Map[String, Double]] = {
    var theList: List[Map[String, Double]] = List()
    val statement = connection.prepareStatement("SELECT * FROM Projectiles WHERE user=?")
    for(i <- Projectiles){
      if(i("user") == name){
        var thisMap: Map[String, Double] = Map()
        val x: Double = i("x").toDouble
        val y: Double = i("y").toDouble
        thisMap = Map("x"-> x, "y"-> y)
        theList=theList:+thisMap
      }
    }
    theList
  }

  def getPlayerProjectilesDatabase(name: String): List[Map[String, Double]] = {
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
    for(player <- Players.keys){
      val name: String = player
      val x: String = Players(player)("x").toString
      val y: String = Players(player)("y").toString
      val kills: String = Players(player)("kills").toString
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


  def getAllPlayersDatabase(): List[Map[String, String]] = {
    // fix this to add projectiles and whatnot
    var theList: List[Map[String, String]] = List()
    // var theMap: Map[String, String] = Map()
    val result: ResultSet = connection.createStatement().executeQuery("SELECT * FROM Player")
    while (result.next()){
      val name: String = result.getString("theName")
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

*/
}


