package chat

import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json, Writes}

trait SocketMessageType {

  def description: String

}

sealed trait ToServerSocketMessageType extends SocketMessageType {

  def socketMessage(msg: JsValue): SocketMessage

}

object ToServerSocketMessageType {

  case object ToServerChat extends ToServerSocketMessageType {
    override val description = "toServerChat"
    override def socketMessage(msg: JsValue): ToServerChatMessage = ToServerChatMessage(
      (msg \ "recipient").validate[String].getOrElse(""),
      (msg \ "text").validate[String].getOrElse("")
    )
  }

  case object ToServerRequestMessages extends ToServerSocketMessageType {
    override val description = "toServerRequestMessages"
    override def socketMessage(msg: JsValue): ToServerRequestMessagesMessage = ToServerRequestMessagesMessage(
     (msg \ "afterDateTimeInMillis").asOpt[Int].map( millis => new DateTime(millis))
   )
  }

  private val socketMessageTypeFrom = Map[String, ToServerSocketMessageType](
    ToServerChat.description -> ToServerChat
  )

  def from(description:String): ToServerSocketMessageType = socketMessageTypeFrom(description)

}

object SocketMessageType {

  case object ToClientChat extends SocketMessageType {
    override val description = "toClientChat"
  }

  case object ToClientMessagesSince extends SocketMessageType {
    override val description = "toClientMessagesSince"
  }

  implicit object SocketMessageTypeWrites extends Writes[SocketMessageType] {
    override def writes(socketMessageType: SocketMessageType) = Json.toJson(socketMessageType.description)
  }

}

trait SocketMessage {

  def socketMessageType: SocketMessageType

}
