package BackEnd

case class SendJSON(message: String)

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.io.{IO, Tcp}
import akka.util.ByteString

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



class tcpServer(theGameActor: ActorRef) extends Actor{

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", 8000))

  // var clients: Set[ActorRef] = Set()
  var theServer: ActorRef = _
  var theBuffer: String = ""
  var theDelimiter: String = "~"


  override def receive: Receive = {
    // case b: Bound => println("Listening on port: " + b.localAddress.getPort)
    case c: Connected =>
      // println("Client Connected: " + c.remoteAddress)

      // make a new game actor for each player here? (using their sender() actor ref)
      // if there is a new player, make a new actor to run their inputs?


      // this will be solely for the server

      // this.clients = this.clients + sender()
      this.theServer = sender()
      this.theServer ! Register(self) // this establishes the connection
    case PeerClosed =>
      // println("Client Disconnected: " + sender())
      // this.clients = this.clients - sender()
      this.theServer = _
    case r: Received =>
      // println("Received: " + r.data.utf8String)
      // this will be a json with input
      theBuffer += r.data.utf8String


      //check this out, don't know what this does yet, but hopefully it reads the json correctly
      while (theBuffer.contains(theDelimiter)) {
        val jsonMessage = theBuffer.substring(0, theBuffer.indexOf(theDelimiter))
        theBuffer = theBuffer.substring(theBuffer.indexOf(theDelimiter) + 1)


        //handleMessageFromWebServer(curr)



        theGameActor ! giveJSON(jsonMessage)
        // do something with jsonMessage
        // the gameActor from main method will be sent this json string

      }





    case send: SendJSON =>
      // println("Sending: " + send.message)
      // this.clients.foreach((client: ActorRef) => client ! Write(ByteString(send.message)))
      this.theServer ! Write(ByteString(send.message))
  }

}
