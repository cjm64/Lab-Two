package BackEnd

// case class SendJSON(message: String)

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import BackEnd.Methods.databaseMethods._
import play.api.libs.json.{JsValue, Json}

// import play.api.libs.json.{JsValue, Json}

/**
  *
  * var jason = {
  * 'name' : name,
  * 'vertical' : 0,     (either -1, 0, or 1)
  * 'horizontal' : 0,   (either -1, 0, or 1)
  * 'angle': null
  * }
  */



class TcpToPy(theGameActor: ActorRef) extends Actor{

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", 8000))

  // var clients: Set[ActorRef] = Set()
  var theServer: Set[ActorRef] = Set()
  var theBuffer: String = ""
  var theDelimiter: String = "~"


  override def receive: Receive = {
    case b: Bound => println("Listening on port: " + b.localAddress.getPort)
    case c: Connected =>
      println("Client Connected: " + c.remoteAddress)
      // this will be solely for the server
      // this.clients = this.clients + sender()
      this.theServer += sender()
      sender() ! Register(self) // this establishes the connection
      theGameActor ! foundTheServer
    case PeerClosed =>
      println("Client Disconnected: " + sender())
      this.theServer = this.theServer - sender()
      // this.theServer = _
    case r: Received =>
      // println("Received: " + r.data.utf8String)
      // this will be a json with input
      theBuffer += r.data.utf8String

      //check this out, don't know what this does yet, but hopefully it reads the json correctly
      while (theBuffer.contains(theDelimiter)) {
        val jsonMessage = theBuffer.substring(0, theBuffer.indexOf(theDelimiter))
        theBuffer = theBuffer.substring(theBuffer.indexOf(theDelimiter) + 1)

        val parsed: JsValue = Json.parse(jsonMessage)
        val theAction: String = (parsed \ "action").as[String]
        if(theAction == "disconnect"){
          val username: String = (parsed \ "username").as[String]
          theGameActor ! disconnectUser(username)
        }else if(theAction == "regular"){
          val regularParsed: Map[String, JsValue] = (parsed \ "data").as[Map[String, JsValue]]
          val theRegularJSON: String = Json.stringify(Json.toJson(regularParsed))
          theGameActor ! giveJSON(theRegularJSON)
        }

        // theGameActor ! giveJSON(jsonMessage)
        // do something with jsonMessage
        // the gameActor from main method will be sent this json string

      }


    case `giNewJSON` => theGameActor ! giveNewJSON
      // println("Sending giveNewJSON to gameActor")
    //   theGameActor ! askBackForJSON


    case send: SendJSON =>
      println("Sending: " + send.message)
      this.theServer.foreach((client: ActorRef) => client ! Write(ByteString(send.message+theDelimiter)))
      // this.theServer ! Write(ByteString(send.message+theDelimiter))
    // the py server is sent the json message
  }

}
object TcpToPy {

  def main(args: Array[String]): Unit = {
    setTable()

    val actorSystem = ActorSystem()

    import actorSystem.dispatcher

    import scala.concurrent.duration._

    val theGameActor = actorSystem.actorOf(Props(classOf[gameActor]))
    val server = actorSystem.actorOf(Props(classOf[TcpToPy], theGameActor))


    actorSystem.scheduler.schedule(16.milliseconds, 32.milliseconds, theGameActor, Update)  // Tells gameActor to update itself
    actorSystem.scheduler.schedule(32.milliseconds, 32.milliseconds, server, giNewJSON) // Tells tcp to send the json
  }

}
