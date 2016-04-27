package user

trait UserDAO {

  def by(id:String):Option[User]

}
