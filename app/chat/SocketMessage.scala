package chat

import play.api.libs.json.{JsValue, Json, Writes}

sealed trait SocketMessageType {

  def description: String

  def socketMessage(msg: JsValue): SocketMessage

}

object SocketMessageType {

  case object ToClientChat extends SocketMessageType {
    override val description = "toClientChat"
    override def socketMessage(msg: JsValue): ToClientChatMessage = null
  }

  case object ToServerChat extends SocketMessageType {
    override val description = "toServerChat"
    override def socketMessage(msg: JsValue): ToServerChatMessage = ToServerChatMessage(
      (msg \ "recipient").validate[String].getOrElse(""),
      (msg \ "text").validate[String].getOrElse("")
    )
  }

  private val socketMessageTypeFrom = Map[String, SocketMessageType](
                                                                      ToClientChat.description -> ToClientChat,
    ToServerChat.description -> ToServerChat
  )

  def from(description:String): SocketMessageType = socketMessageTypeFrom(description)

  implicit object SocketMessageTypeWrites extends Writes[SocketMessageType] {
    override def writes(socketMessageType: SocketMessageType) = Json.toJson(socketMessageType.description)
  }

}

trait SocketMessage {

  def socketMessageType: SocketMessageType

}
