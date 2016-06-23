package util

import java.util.UUID

object TestUUIDProviderImpl extends UUIDProvider {

  private var _index:Int = 0

  def index(newIndex:Int):Int = {
    _index = newIndex
    _index
  }

  override def randomUUID():UUID = {
    _index += 1
    UUID.fromString("00000000-0000-0000-0000-" + f"${_index}%012d")
  }

}
