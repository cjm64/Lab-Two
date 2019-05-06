package tests

import org.scalatest._
import scala.collection.mutable.Map
import BackEnd.Methods.Methods

  class backendTests extends FunSuite {

    test("test player exists false and make player"){
      val test1 = Methods.playerExists("player1")
      assert(test1 == false)
    }

    test("test player exists true"){
      Methods.Players = Map("player1" -> Map("x" -> "34.143", "y" -> "", "kills" -> "0", "lastUpdate" -> "", "theName" -> "", "inputX" -> "0", "inputY" -> "0"))
      val test1 = Methods.playerExists("player1")
      assert(test1 == true)
    }

    test("test make player"){
      val test1 = Methods.makePlayer("player1")
      val test2 = Methods.makePlayer("player2")
      assert(Methods.Players("player1")("kills") == "0")
      assert(Methods.Players("player2")("kills") == "0")
      assert(Methods.Players("player1")("inputX") == "0")
      assert(Methods.Players("player2")("inputX") == "0")
      assert(Methods.Players("player1")("inputY") == "0")
      assert(Methods.Players("player2")("inputY") == "0")
    }

  }
