package contact

import chat.ToServerSocketMessageType.ToServerRequestContacts
import chat.{SocketMessage, SocketMessageType}
import com.google.inject.Singleton

@Singleton
class ToServerRequestContactsMessage extends SocketMessage {

  override val socketMessageType: SocketMessageType = ToServerRequestContacts

}

object ToServerRequestContactsMessage {
  def apply: ToServerRequestContactsMessage = new ToServerRequestContactsMessage()
}