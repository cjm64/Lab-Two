package BackEnd

import play.api.libs.json.{JsValue, Json}
import java.sql.{Connection, DriverManager, ResultSet}
import akka.actor.{Actor, ActorRef, Props}
import BackEnd.Methods.ActualDatabaseMethods._

class ActualUpdateDatabase extends Actor{


  def existsPlayer(name: String): Boolean = {
    val statement = connection.prepareStatement("SELECT * FROM Player WHERE theName=?")
    statement.setString(1, name)
    val result: ResultSet = statement.executeQuery()
    result.next()
  }


  def saveToDatabase(): Unit = {

    // Players is Map[String, Map[String, String]]
    for(player <- Players.keys){
      val x: Double = Players(player)("x").toDouble
      val y: Double = Players(player)("y").toDouble
      val kills: Int = Players(player)("kills").toInt
      val lastUpdate: Long = Players(player)("lastUpdate").toLong
      val inputX: Int = Players(player)("inputX").toInt
      val inputY: Int = Players(player)("inputY").toInt

      if(existsPlayer(player)){
        val statement = connection.prepareStatement("UPDATE Player SET x=?, y=?, kills=?, deaths=?, lastUpdate=?, inputX=?, inputY=? WHERE theName=?")

        statement.setDouble(1, x)  // x
        statement.setDouble(2, y)  // y
        statement.setInt(3, kills)       // kills
        statement.setInt(4, 0)       // deaths
        statement.setLong(5, lastUpdate)
        statement.setDouble(6, inputX)
        statement.setDouble(7, inputY)
        statement.setString(8, player)
        statement.execute()
      }else{
        val statement = connection.prepareStatement("INSERT INTO Player VALUE (?, ?, ?, ?, ?, ?, ?, ?)")

        statement.setDouble(1, x)  // x
        statement.setDouble(2, y)  // y
        statement.setInt(3, kills)       // kills
        statement.setInt(4, 0)       // deaths
        statement.setLong(5, lastUpdate)
        statement.setString(6, player)
        statement.setInt(7, inputX)
        statement.setInt(8, inputY)
        statement.execute()
      }
    }

    val result: ResultSet = connection.createStatement().executeQuery("SELECT * FROM Player")
    while(result.next()){
      var inDict: Boolean = false
      for (player <- Players.keys){
        if(player == result.getString("theName")){
          inDict = true
        }
      }
      if(!inDict){
        val statement = connection.prepareStatement("DELETE FROM Player WHERE theName=?")
        statement.setString(1, result.getString("theName"))
        statement.execute()
      }
    }
  }


  def loadDictionary(): Unit = {

    val result: ResultSet = connection.createStatement().executeQuery("SELECT * FROM Player")
    Players = Map()
    while(result.next()){
      val theName: String = result.getString("theName")
      val x: String = result.getDouble("x").toString
      val y: String = result.getDouble("y").toString
      val kills: String = result.getInt("kills").toString
      val lastUpdate: String = result.getLong("lastUpdate").toString
      val inputX: String = result.getInt("inputX").toString
      val inputY: String = result.getInt("inputY").toString
      Players += theName -> Map(
        "x" -> x,
        "y" -> y,
        "kills" -> kills,
        "lastUpdate" -> lastUpdate,
        "inputX" -> inputX,
        "inputY" -> inputY
      )
    }



  }

  override def receive: Receive = {

    case `updateTheDatabase` => saveToDatabase()
    case `loadToDictionary` => loadDictionary()

  }

}
