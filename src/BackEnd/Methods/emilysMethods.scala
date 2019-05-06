package BackEnd.Methods

object emilysMethods {
  var players: Map[String, Map[String, String]] = Map()
  var projectiles: List[Map[String, String]] = List()
  var id = 1
  def playerExists(name: String): Boolean = {
    for((k, v) <- players){
      if(k == name){
        return true
      }
    }
    false
  }


  def makePlayer(name: String): Unit = {
    if(playerExists(name) == false){
      val xLoc: String = (math.random() * 800).toString
      val yLoc: String = (math.random() * 800).toString
      val kills: String = 0.toString
      val lUpdate: String = System.nanoTime().toString
      val n = name
      val iX: String = 0.toString
      val iY: String = 0.toString
      var pMap: Map[String, String] = Map()
      pMap = pMap + ("x" -> xLoc)
      pMap = pMap + ("y" -> yLoc)
      pMap = pMap + ("kills" -> kills)
      pMap = pMap + ("lastUpdate" -> lUpdate)
      pMap = pMap + ("theName" -> n)
      pMap = pMap + ("inputX" -> iX)
      pMap = pMap + ("inputY" -> iY)
      players = players + (name -> pMap)
    }
  }

  def makeProjectile(name: String, angle: Double): Unit = { // it only returns an int so that gameActor can send new projectiles
    val u = name
    id += 1
    val pid: String = id.toString()
    val x = players(name)("x")
    val y = players(name)("y")
    val a: String = angle.toString()
    val lu: String = System.nanoTime().toString
    val lt: String = 0.0.toString
    var pMap: Map[String, String] = Map()
    pMap = pMap + ("user" -> u)
    pMap = pMap + ("id" -> pid)
    pMap = pMap + ("x" -> x)
    pMap = pMap + ("y" -> y)
    pMap = pMap + ("angle" -> a)
    pMap = pMap + ("lastUpdate" -> lu)
    pMap = pMap + ("lifetime" -> lt)
    projectiles = projectiles :+ pMap
  }
}
