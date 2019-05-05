package ScalaTests



import java.net.Socket
import java.net.Socket._

import org.scalatest._

class HandleMessagesFromPython() extends Emitter.Listener {
  override def call(objects: Object*): Unit = {
    val message = objects.apply(0).toString
    // do something with message
  }
}


class ServerTest extends Funsuite{
  test(){
    var socket: Socket = new java.net.Socket("127.0.0.1", 8023)
    socket.emit("register", "myUsername")
  }
}
