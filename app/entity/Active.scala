package entity

trait Active[T <: Active[T]] {

  def isActive:Boolean

}
