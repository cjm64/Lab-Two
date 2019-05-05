package BackEnd


case class giveJSON(theJSON: String)
case object giveNewJSON
case class SendJSON(message: String)
case object Update
case class disconnectUser(user: String)
