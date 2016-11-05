package contact

import chat.ToServerSocketMessageType.ToServerAddContact
import chat.{SocketMessage, SocketMessageType}

case class ToServerAddContactMessage(usernameOfContactToAdd: String) extends SocketMessage {

  override def socketMessageType: SocketMessageType = ToServerAddContact

}
