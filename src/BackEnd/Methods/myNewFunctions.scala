package BackEnd.Methods

object myNewFunctions {
  /*
  var Projectiles: List[Map[String, String]] = List()
  var Players: Map[String, Map[String, String]] = Map()



  def checkLifeTime(id: Int): Unit = {
    val lifeTime: Double = 2.0 // seconds
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

  def updatePlayerInput(name: String, vert: Int, horiz: Int): Unit = {
    Players(name)("inputX") = horiz.toString
    Players(name)("inputY") = vert.toString
  }

  def getPlayerProjectiles(name: String): List[Map[String, Double]] = {
    var theList: List[Map[String, Double]] = List()
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

  def getAllPlayers(): List[Map[String, String]] = {
    // fix this to add projectiles and whatnot
    var theList: List[Map[String, String]] = List()
    // var theMap: Map[String, String] = Map()
    for(player <- Players.keys){
      val name: String = player
      val x: String = Players(player)("x")
      val y: String = Players(player)("y")
      val kills: String = Players(player)("kills")
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
*/
}
