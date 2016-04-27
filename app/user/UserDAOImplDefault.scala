package user

class UserDAOImplDefault extends UserDAO {

  override def by(id:String):Option[User] = Some(User(id, "default", "default"))

}

class UserDAOImplAlternative extends UserDAO {

  override def by(id:String):Option[User] = Some(User(id, "alternative", "alternative"))

}