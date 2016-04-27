package fetcher

import entity.Active

case class TestActive(override val isActive:Boolean) extends Active[TestActive]