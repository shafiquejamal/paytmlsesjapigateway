package user

import scalikejdbc._

import scala.util.{Success, Try}

class ScalikeJDBCUserDAO extends UserDAO {

  override def byUserName(userName:String) = UserByUserName(userName)
  override def byEmail(email:String) = UserByEmail(email)
  override def add(user:User):Try[User] = addUser(user:User)

  def UserByUserName(userName:String)(implicit session: DBSession = ReadOnlyAutoSession):Option[User] = {
    by(sql"select id, name, email, username, isactive, password, created from xuser where username = ${userName} order by created desc limit 1")(session)
  }

  def UserByEmail(email:String)(implicit session: DBSession = ReadOnlyAutoSession):Option[User] = {
    by(sql"select id, name, email, username, isactive, password, created from xuser where email = ${email} order by created desc limit 1")
  }

  private def by(sqlQuery:SQL[_, _])(implicit session: DBSession = ReadOnlyAutoSession):Option[User] = {
    sqlQuery
    .map { rs =>
      new User(
                id = Option(rs.string("id")),
                userName = Option(rs.string("username")),
                email = rs.string("email"),
                password = rs.string("password"),
                isActive = rs.boolean("isactive"),
                created = Option(rs.jodaDateTime("created"))
              )
    }
    .single.apply().filter(_.isActive)
  }

  def addUser(user:User)(implicit session: DBSession = AutoSession):Try[User] = {
    sql"insert into xuser (username, email, password, isactive) values (${user.userName}, ${user.email}, ${user.password}, ${user.isActive})"
    Success(user)
  }

}