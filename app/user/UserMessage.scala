package user

import java.util.UUID

case class UserMessage(maybeId:Option[UUID], maybeUsername:Option[String], email:String)
