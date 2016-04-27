package user

trait UserAPI {

  def find(id:String):Option[UserMessage]

}
