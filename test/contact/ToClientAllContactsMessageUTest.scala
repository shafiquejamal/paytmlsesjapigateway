package contact

import org.scalatest.{FlatSpecLike, ShouldMatchers}
import play.api.libs.json.Json

class ToClientAllContactsMessageUTest extends FlatSpecLike with ShouldMatchers {

  "Converting to JSON" should "work" in {

    Json.toJson(ToClientAllContactsMessage(Seq("some_user", "another_user"))) shouldEqual Json.obj(
      "payload" -> Json.arr("some_user","another_user"),
      "socketMessageType" -> "UPDATE_CONTACTS"
    )

  }

}
