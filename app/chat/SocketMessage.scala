package chat

import play.api.libs.json.{Json, JsValue, Writes}

sealed trait SocketMessageType { def description: String }

object SocketMessageType {

  case object ChatMessage extends SocketMessageType { override val description = "chatMessage"}

  val socketMessageTypeFrom = Map[String, SocketMessageType](
    ChatMessage.description -> ChatMessage
  )

  def create(description:String): SocketMessageType = socketMessageTypeFrom(description)

  implicit object SocketMessageTypeWrites extends Writes[SocketMessageType] {
    override def writes(socketMessageType: SocketMessageType) = socketMessageType match {
      case ChatMessage => Json.toJson(ChatMessage.description)
    }
  }

}

trait SocketMessage {

  def socketMessageType: SocketMessageType

}
