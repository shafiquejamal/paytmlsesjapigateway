package chat

import org.scalatest.{FlatSpecLike, ShouldMatchers}
import play.api.libs.json.Json
import socket.SocketMessageType.ToClientChat
import user.UserFixture

class ToClientChatMessageUTest
  extends FlatSpecLike
  with ShouldMatchers
  with UserFixture {

  "Converting a ToClientChatMessage to JSON" should "work" in {

    val toClientChatMessage = ToClientChatMessage(Chat(idMsgAliceBob1, "alice", "bob", "alice to bob one", now.getMillis))

    val expected =  Json.obj("socketMessageType" -> "RECEIVE_MESSAGE",
      "payload" -> Json.obj(
      "id" -> "00000000-0000-0000-0000-100000000013",
      "from" -> "alice",
      "to" -> "bob",
      "text" -> "alice to bob one",
      "time" -> now.getMillis))

    toClientChatMessage.socketMessageType shouldEqual ToClientChat
    Json.toJson(toClientChatMessage) shouldEqual expected
    toClientChatMessage.toJson shouldEqual expected

  }


}
