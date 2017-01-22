package clientmessaging

import java.util.UUID

object ClientPaths {

  private def prefix(id: UUID) = s"namedClient_${id.toString}_"

  def namedClientActorName(clientId: UUID, randomUUID: UUID): String = s"${prefix(clientId)}${randomUUID.toString}"

  def namedClientPath(clientId: UUID): String = s"/user/*/flowActor/*/${prefix(clientId)}*"
  
}
