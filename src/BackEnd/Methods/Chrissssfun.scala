package BackEnd.Methods

object Chrissssfun {

  var Players: Map[String, Map[String, String]] = Map()
  var Projectiles: List[Map[String,String]] = List()

  def updatePlayerPosition(name: String, vert: Int, horis: Int): Unit = {
    val speed: Double = 50.0 // 50 px / second
    Players(name)("inputX") = horis.toString
    Players(name)("inputY") = vert.toString
    val time = Players(name)("lastUpdate").toLong
    val deltaS = (System.nanoTime() - time) / 1000000000.0
    Players(name)("x") = (Players(name)("x").toDouble + (Players(name)("inputX").toDouble  * deltaS * speed)).toString
    Players(name)("y") = (Players(name)("y").toDouble + (Players(name)("inputY").toDouble  * deltaS * speed)).toString
    Players(name)("lastUpdate") = System.nanoTime().toString
  }

  def updateProjectilePos(id: Int): Unit = {
    val speed: Double = 100.0
    for(p <- Projectiles){
      if(p("id").toInt == id){
        val angle = p("angle").toDouble
        val time = p("lastUpdate").toLong
        val deltaS = (System.nanoTime() - time)/1000000000.0
        p("x") = (p("x").toDouble + (math.sin(angle)*speed*deltaS)).toString
        p("y") = (p("y").toDouble + (math.cos(angle)*speed*deltaS)).toString
        p("lastUpdate") = System.nanoTime().toString
        p("lifetime") = (p("lifetime").toDouble + deltaS).toString
      }
    }
  }

  def collision(name: String, id: Int): Unit = {
    var killer = ""
    Players(name)("x") = (math.random() * 800).toString
    Players(name)("y") = (math.random() * 800).toString
    for(p <- Projectiles){
      if (p("id").toInt == id){
        killer = p("user")
        Projectiles = Projectiles.filter(_ != p)
      }
    }
    Players(killer)("kills") = (Players(killer)("kills").toDouble + 1).toString
  }

  def checkCollision(): Map[Int, String] = {
    var theMap: Map[Int, String] = Map()
    val playerRadius: Double = 16.0
    for((k,v) <- Players){
      for(p <- Projectiles){
        if(p("user") != k) {
          val d: Double = math.sqrt(math.pow(p("x").toDouble - v("x").toDouble, 2) - math.pow(p("y").toDouble - v("y").toDouble, 2))
          if (d < playerRadius) {
            theMap = theMap + (p("id").toInt -> k)
          }
        }
      }
    }
    theMap
  }

  def deletePlayer(name: String): Unit = {
    for(p <- Projectiles){
      if(p("user") == name){
        Projectiles = Projectiles.filter(_ != p)
      }
    }
    Players -= name
  }

}
