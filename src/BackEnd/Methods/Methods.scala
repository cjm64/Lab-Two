package BackEnd.Methods

import BackEnd.scalaClasses.{Player, Projectile, theWorld}

import play.api.libs.json.{JsValue, Json}
import java.sql.{Connection, DriverManager, ResultSet}
import scala.collection.mutable

// projectileMap += id -> List(projX, projY, projUser)
object Methods {

  var Players: mutable.Map[String, mutable.Map[String, String]] = mutable.Map()
  var Projectiles: List[mutable.Map[String, String]] = List()
  var id = 1

  def playerExists(name: String): Boolean = {
    for((k, v) <- Players){
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
      var pMap: mutable.Map[String, String] = mutable.Map()
      pMap = pMap + ("x" -> xLoc)
      pMap = pMap + ("y" -> yLoc)
      pMap = pMap + ("kills" -> kills)
      pMap = pMap + ("lastUpdate" -> lUpdate)
      pMap = pMap + ("theName" -> n)
      pMap = pMap + ("inputX" -> iX)
      pMap = pMap + ("inputY" -> iY)
      Players = Players + (name -> pMap)
    }
  }


  def makeProjectile(name: String, angle: Double): Unit = { // it only returns an int so that gameActor can send new projectiles
    val u = name
    id += 1
    val pid: String = id.toString
    val x = Players(name)("x")
    val y = Players(name)("y")
    val a: String = angle.toString
    val lu: String = System.nanoTime().toString
    val lt: String = 0.0.toString
    var pMap: mutable.Map[String, String] = mutable.Map()
    pMap = pMap + ("user" -> u)
    pMap = pMap + ("id" -> pid)
    pMap = pMap + ("x" -> x)
    pMap = pMap + ("y" -> y)
    pMap = pMap + ("angle" -> a)
    pMap = pMap + ("lastUpdate" -> lu)
    pMap = pMap + ("lifetime" -> lt)
    Projectiles = Projectiles :+ pMap
  }





  // projectileMap += id -> List(projX, projY, projUser)

  def updatePlayerPosition(name: String, vert: Int, horis: Int): Unit = {
    val speed: Double = 80.0 // 50 px / second

    Players(name)("inputX") = horis.toString
    Players(name)("inputY") = vert.toString
    val time = Players(name)("lastUpdate").toLong
    val deltaS = (System.nanoTime() - time) / 1000000000.0
    Players(name)("x") = (Players(name)("x").toDouble + (Players(name)("inputX").toDouble  * deltaS * speed)).toString
    Players(name)("y") = (Players(name)("y").toDouble + (Players(name)("inputY").toDouble  * deltaS * speed)).toString
    Players(name)("lastUpdate") = System.nanoTime().toString
  }

  def updateProjectilePos(id: Int): Unit = {
    val speed: Double = 160.0
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

  // LOOK HERE, I DONT KNOW ABOUT FILTER
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
    Players(killer)("kills")= (Players(killer)("kills").toDouble + 1).toString
  }

  def checkCollision(): mutable.Map[Int, String] = {
    var theMap: mutable.Map[Int, String] = mutable.Map()
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

  def CheckOutOfBounds(name: String): Unit = {
    val worldSize: Double = 800.0
    val result: mutable.Map[String, String] = Players(name)
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


  def deletePlayer(name: String): Unit = {
    for(p <- Projectiles){
      if(p("user") == name){
        Projectiles = Projectiles.filter(_ != p)
      }
    }
    Players -= name
  }





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
    //println(Players)
  }

  def getPlayerProjectiles(name: String): List[mutable.Map[String, Double]] = {
    var theList: List[mutable.Map[String, Double]] = List()
    for(i <- Projectiles){
      if(i("user") == name){
        var thisMap: mutable.Map[String, Double] = mutable.Map()
        val x: Double = i("x").toDouble
        val y: Double = i("y").toDouble
        thisMap = mutable.Map("x"-> x, "y"-> y)
        theList=theList:+thisMap
      }
    }
    theList
  }



  def getAllPlayers(): List[mutable.Map[String, String]] = {
    // fix this to add projectiles and whatnot
    var theList: List[mutable.Map[String, String]] = List()
    // var theMap: mutable.Map[String, String] = mutable.Map()
    for(player <- Players.keys){
      val name: String = player
      val x: String = Players(player)("x")
      val y: String = Players(player)("y")
      val kills: String = Players(player)("kills")
      // val deaths: String = result.getInt("deaths").toString
      // val projectiles: List[Map[String, Double]] = getPlayerProjectiles(name)
      theList=theList:+mutable.Map(
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




}