package contact

import org.scalatest.{FlatSpecLike, ShouldMatchers}
import play.api.libs.json.Json

class ToClientAllContactsMessageUTest extends FlatSpecLike with ShouldMatchers {

  "Converting to JSON" should "work" in {

    val message = ToClientAllContactsMessage(Seq("some_user", "another_user"))
    val expected = Json.obj(
      "payload" -> Json.arr("some_user","another_user"),
      "socketMessageType" -> "UPDATE_CONTACTS"
    )

    Json.toJson(message) shouldEqual expected

    message.toJson shouldEqual expected
  }

}
