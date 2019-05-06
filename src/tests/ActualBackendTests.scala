package tests

import org.scalatest._
import scala.collection.mutable.Map
import BackEnd.Methods.ActualMethods

class ActualBackendTests extends FunSuite {

  test("test player exists false and make player"){
    val test1 = ActualMethods.playerExists("player1")
    assert(test1 == false)
  }

  test("test player exists true"){
    ActualMethods.Players = Map("player1" -> Map("x" -> "34.143", "y" -> "", "kills" -> "0", "lastUpdate" -> "", "theName" -> "", "inputX" -> "0", "inputY" -> "0"))
    val test1 = ActualMethods.playerExists("player1")
    assert(test1 == true)
  }

  test("test make player"){
    val test1 = ActualMethods.makePlayer("player1")
    val test2 = ActualMethods.makePlayer("player2")
    assert(ActualMethods.Players("player1")("kills") == "0")
    assert(ActualMethods.Players("player2")("kills") == "0")
    assert(ActualMethods.Players("player1")("inputX") == "0")
    assert(ActualMethods.Players("player2")("inputX") == "0")
    assert(ActualMethods.Players("player1")("inputY") == "0")
    assert(ActualMethods.Players("player2")("inputY") == "0")
  }

}
