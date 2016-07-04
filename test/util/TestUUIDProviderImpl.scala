package util

import java.util.UUID

object TestUUIDProviderImpl extends UUIDProvider {

  var index:Int = 0

  override def randomUUID():UUID = {
    index += 1
    UUID.fromString("00000000-0000-0000-0000-" + f"$index%012d")
  }

}
