package fetcher

import entity.Active

trait Fetcher[T <: Active[T]] {

  def keepIfActive(maybeEntity: Option[T]):Option[T] = maybeEntity.filter(_.isActive)

}
