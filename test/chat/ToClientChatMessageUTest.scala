package chat

import chat.SocketMessageType.ToClientChat
import org.scalatest.{FlatSpecLike, ShouldMatchers}
import play.api.libs.json.Json
import user.UserFixture

class ToClientChatMessageUTest
  extends FlatSpecLike
  with ShouldMatchers
  with UserFixture {

  "Converting a ToClientChatMessage to JSON" should "work" in {

    val toClientChatMessage = ToClientChatMessage(idMsgAliceBob1, "alice", "bob", "alice to bob one", now.getMillis)

    toClientChatMessage.socketMessageType shouldEqual ToClientChat
    Json.toJson(toClientChatMessage) shouldEqual Json.obj(
      "id" -> "00000000-0000-0000-0000-100000000013",
      "socketMesageType" -> "toClientChat",
      "from" -> "alice",
      "to" -> "bob",
      "text" -> "alice to bob one",
      "time" -> now.getMillis)
  }


}
