package BackEnd


case class giveJSON(theJSON: String)
case object giveNewJSON
case class SendJSON(message: String)
case object Update
case class disconnectUser(user: String)
case object foundTheServer
// case object askBackForJSON
case object giNewJSON
case object updateTheDatabase
case object loadToDictionary

