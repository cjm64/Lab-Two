package BackEnd.Methods

import BackEnd.scalaClasses.{Player, Projectile, theWorld}

import play.api.libs.json.{JsValue, Json}
import java.sql.{Connection, DriverManager, ResultSet}

object Methods {



  // make this a circle around the player with radius 16?

  // use distance formula and if the distance is smaller than the radius of the circle, then collision
  def hitDetection(player: Player, projectile: Projectile): Boolean = {
    // returns true if there is collision
    if(player.x > projectile.x + projectile.sizeX || projectile.x > player.x + player.sizeX){
      false
    }else if(player.y > projectile.y + projectile.sizeY || projectile.y > player.y + player.sizeY){
      false
    }else{
      true
    }
  }


  def offMapDetection(player: Player, world: theWorld): Boolean = {
    // returns true if the player is out of bounds
    if(player.x < 0 || player.x > world.boundX){
      true
    }else if(player.y < 0 || player.y > world.boundY){
      true
    }else{
      false
    }
  }

  /**
    *
    * var jason = {
    * 'name' : name,
    * 'vertical' : 0,     (either -1, 0, or 1)
    * 'horizontal' : 0,   (either -1, 0, or 1)
    * 'angle': null
    * }
    */

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









// this is big boi method
  def useJSON(jsonString: String): Unit = {

    val parsed: JsValue = Json.parse(jsonString)

    val name: String = (parsed \ "name").as[String]        // this tell us which sprite to update and what sprite to fire from
    val vertical: Int = (parsed \ "vertical").as[Int]      // 0 - nothing, -1 - down, 1 - up
    val horizontal: Int = (parsed \ "horizontal").as[Int]  // 0 - nothing, -1 - left, 1 - right
    val angle: Double = (parsed \ "angle").as[Double]      // if this is not null, do something with it



    // now we need to connect to the database and see if the name is in the database
    // if it is, update its info using these values
    // else, you should make an entry in the table
      // if new entry, create a random spawn location
    // adjust the movement by 1 in the correct direction (unless 0 OR if they'd go off the map)
    // now check if any projectiles collide with players
      // if they do collide, then delete both and adjust data accordingly

    // now that we know the user is in the table, check if angle is null
    // if angle == null, do nothing
    // if it is not, make a new projectile using that angle and the players position(their position plus some)



    // now that we calculated the information, we need to send the information in a new json
  }
  def makeJSON(): JsValue = {

    // note that this is not what the database looks like
    var thedatabase: Map[String, Int] = Map("id" -> 1)

    var someList: List[Map[String, JsValue]] = List()

    for (player <- thedatabase){
      // for()
      var projectile: JsValue = Json.toJson("I just declared projectile")

      if(Json.toJson("angle != null") == Json.toJson("spaghetti")){
        projectile = Json.toJson(Map(
          "x" -> 0.0, // double from table
          "y" -> 0.0, // double from table
          "angle" -> 0.0 // double from table
        ))
      }else{
        // we might have to do above, but with nulls
        projectile = Json.toJson(null)
      }

      someList = someList:+Map(
        "Name" -> Json.toJson(player),
        "Kills" -> Json.toJson("from database under player"),
        "Deaths" -> Json.toJson("from database under player"),
        "x" -> Json.toJson("from database under player"),
        "y" -> Json.toJson("from database under player"),
        "Projectile" -> projectile
      )
    }

    var theMap: Map[String, List[Map[String, JsValue]]] = Map(
      "PlayerData" -> someList
    )

    Json.toJson(theMap)
  }



  /**
    * player = {
    * "Name": String,
    * "Kills": Int,
    * "Deaths": Int,
    * "x": Double,
    * "y": Double,
    * "Immunity": Boolean,  -- maybe
    * }
    * players = a list of all players and their data in the format above
    *
    * projectile = {
    * "x": Double,
    * "y": Double,
    * "angle": Double
    * }
    *
    * newProjectiles = a list of all newly created projectiles and their data in the format above
    * (this will be empty if there are no new projectiles)
    *
    *
    * backToFrontJSON = {
    * "PlayerData":  players,
    * "NewProjectiles": newProjectiles,
    * }
    */

