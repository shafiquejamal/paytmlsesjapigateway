package util

import java.util.UUID

class UUIDProviderImpl extends UUIDProvider {

  override def randomUUID(): UUID = UUID.randomUUID()
  
}

object UUIDProviderImpl {
  def apply() = new UUIDProviderImpl()
}
