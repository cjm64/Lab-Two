package BackEnd

import play.api.libs.json.{JsValue, Json}
import java.sql.{Connection, DriverManager, ResultSet}

import akka.actor.{Actor, ActorRef, Props}
import BackEnd.Methods.ActualMethods._
import theGame._

import scala.collection.mutable

class ActualGameActor extends Actor{


  // var newProjectiles: List[Int] = List()
  // var foundServer: Boolean = false

  def useJSON(jsonString: String): Unit = {

    val parsed: JsValue = Json.parse(jsonString)

    val name: String = (parsed \ "name").as[String] // this tell us which sprite to update and what sprite to fire from
    //println(name)
    val vertical: Int = (parsed \ "vertical").as[Int]      // 0 - nothing, -1 - down, 1 - up
    //println(vertical)
    val horizontal: Int = (parsed \ "horizontal").as[Int]  // 0 - nothing, -1 - left, 1 - right
    //println(horizontal)
    val angle: Double = (parsed \ "angle").as[Double]      // if this is not null, do something with it


    if (playerExists(name)){
      updatePlayerInput(name, vertical, horizontal)
    }else{
      makePlayer(name)
    }


    if (angle != 100.0){
      // yes, we make a new projectile, but dont add that to new projectiles
      // we just send all of the projectiles at the end, in make json and whatnot
      makeProjectile(name, angle)
    }

  }





  // now that we calculated the information, we need to send the information in a new json
  def makeJSON(): String = {

    // FIX THIS
    // how do we get all of the new projectiles to send? UPDATE, I did it
    if (theGame.foundTheServer){
      val playerMapping: List[mutable.Map[String, String]] = getAllPlayers()
      var someList: List[mutable.Map[String, JsValue]] = List()
      for (map <- playerMapping){

        val projMap: List[mutable.Map[String, Double]] = getPlayerProjectiles(map("Name"))
        someList = someList:+mutable.Map(
          "Name" -> Json.toJson(map("Name")),
          "Kills" -> Json.toJson(map("Kills")),
          // "Deaths" -> Json.toJson(map("Deaths")),
          "x" -> Json.toJson(map("x")),
          "y" -> Json.toJson(map("y")),
          "Projectile" -> Json.toJson(projMap)
        )
      }
      // println("foundServer: true")
      val theJSON: String = Json.stringify(Json.toJson(mutable.Map("PlayerData" -> someList)))
      // println(theJSON)
      theJSON
    }else{
      // println("foundServer: false")
      "MakeJSON(): found server is false"
    }

  }


  def updateObjects(): Unit = {

    // update projectiles
    if(theGame.foundTheServer){

      for(dict <- Projectiles){
        val id: Int = dict("id").toInt
        updateProjectilePos(id)
        checkLifeTime(id)
      }

      // check collision
      val PlayersToIDs: mutable.Map[Int, String] = checkCollision()
      for (id <- PlayersToIDs.keys){
        collision(PlayersToIDs(id), id) // do collision
      }


      for (theName <- Players.keys){
        val playerDict: mutable.Map[String, String] = Players(theName)
        updatePlayerPosition(theName, playerDict("inputY").toInt, playerDict("inputX").toInt)
        CheckOutOfBounds(theName)
      }
    }else{

    }
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



  override def receive: Receive = {

    case message: giveJSON => useJSON(message.theJSON)
    case message: disconnectUser => deletePlayer(message.user)
    // case `askBackForJSON` =>
    case `giveNewJSON` => sender() ! SendJSON(makeJSON())
    case Update => updateObjects()
    case `foundTheServer` => theGame.setToTrue()





  }





  // make receive method in order to actually run code
  // implement all of the other methods in that receive method



}