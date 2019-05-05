package BackEnd

import play.api.libs.json.{JsValue, Json}
import java.sql.{Connection, DriverManager, ResultSet}
import akka.actor.{Actor, ActorRef, Props}
import BackEnd.Methods.databaseMethods._

class gameActor extends Actor{


  var newProjectiles: List[Int] = List()


  def useJSON(jsonString: String): Unit = {

    val parsed: JsValue = Json.parse(jsonString)

    val name: String = (parsed \ "name").as[String]        // this tell us which sprite to update and what sprite to fire from
    val vertical: Int = (parsed \ "vertical").as[Int]      // 0 - nothing, -1 - down, 1 - up
    val horizontal: Int = (parsed \ "horizontal").as[Int]  // 0 - nothing, -1 - left, 1 - right
    val angle: Double = (parsed \ "angle").as[Double]      // if this is not null, do something with it


    if (playerExists(name)){
      updatePlayerPos(name, vertical, horizontal)
    }else{
      makePlayer(name)
    }


    // we could do this in updateObjects I'm pretty sure...
    // val result: ResultSet = connection.prepareStatement("SELECT * FROM Projectiles").executeQuery()
    // while(result.next()){
    //   updateProjectilePos(result.getInt("id"))
    // } // yeah, I'm pretty sure I can do this in updateObjects

    // I also think that this could be done in updateObjects
    // val collisionMap: Map[Int, String] = checkCollision()
    // for (key <- collisionMap.keys){
    //   collision(collisionMap(key), key)
    // }
    // I also think that think could be done in update Objects


    if (angle != null){
      // yes, we make a new projectile, but dont add that to new projectiles
      // we just send all of the projectiles at the end, in make json and whatnot
      makeProjectile(name, angle)
    }

    // check for out of bounds
    CheckOutOfBounds(name)

    // check for projectile lifetime (but in the updateObjects method)



    // now we need to connect to the database and see if the name is in the database
    // if it is, update its info using these vals
    // else, you should make an entry in the table
    // if new entry, create a random spawn location
    // adjust the movement by 1 in the correct direction (unless 0 OR if they'd go off the map)
    // now check if any projectiles collide with players
    // if they do collide, then delete both and adjust data accordingly
    // now that we know the user is in the table, check if angle is null
    // if angle == null, do nothing
    // if it is not, make a new projectile using that angle and the players position(their position plus some)

  }

  // now that we calculated the information, we need to send the information in a new json
  def makeJSON(): String = {

    // FIX THIS
    // how do we get all of the new projectiles to send? UPDATE, I did it
    val playerMapping: List[Map[String, String]] = getAllPlayers()
    var someList: List[Map[String, JsValue]] = List()
    for (map <- playerMapping){

      val projMap: List[Map[String, Double]] = getPlayerProjectiles(map("Name"))
      someList = someList:+Map(
        "Name" -> Json.toJson(map("Name")),
        "Kills" -> Json.toJson(map("Kills")),
        // "Deaths" -> Json.toJson(map("Deaths")),
        "x" -> Json.toJson(map("x")),
        "y" -> Json.toJson(map("y")),
        "Projectile" -> Json.toJson(projMap)
      )
    }
    Json.stringify(Json.toJson(Map("PlayerData" -> someList)))
  }


  def updateObjects(): Unit = {

    // update projectiles
    val result: ResultSet = connection.prepareStatement("SELECT * FROM Projectiles").executeQuery()
    while(result.next()){
      val theID: Int = result.getInt("id")
      updateProjectilePos(theID)
      checkLifeTime(theID)
    }


    // update players
    // this is based on user input though, so, we should leave this to the useJSON method
    // val resultTwo: ResultSet = connection.prepareStatement("SELECT * FROM Players").executeQuery()

    // check collision
    val PlayersToIDs: Map[Int, String] = checkCollision()
    for (id <- PlayersToIDs.keys){
      collision(PlayersToIDs(id), id) // do collision
    }

    // check boundaries
    // or just do that in useJSON...

    // check projectile lifetime

  }





  override def receive: Receive = {

    case message: giveJSON => useJSON(message.theJSON)
    case message: disconnectUser => deletePlayer(message.user)
    case giveNewJSON => sender() ! SendJSON(makeJSON())
    case Update => updateObjects()






  }





  // make receive method in order to actually run code
  // implement all of the other methods in that receive method



}