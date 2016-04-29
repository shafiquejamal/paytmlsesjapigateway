package entity

trait ActiveFlag[T <: ActiveFlag[T]] {

  def isActive:Boolean

}
