package user

import java.util.UUID

import org.joda.time.DateTime
import scalikejdbc._

import scala.util.{Failure, Success, Try}

class ScalikeJDBCUserDAO extends UserDAO {

  override def byUserName(userName:String) = UserByUserName(userName)
  override def byEmail(email:String) = UserByEmail(email)
  override def addFirstTime(user:User, created:DateTime, uUID: UUID):Try[User] = addUserFirstTime(user:User, created, uUID)

  def UserByUserName(userName:String)(implicit session: DBSession = ReadOnlyAutoSession):Option[User] = {
    by(sql"select id, email, username, isactive, password, created, parentid from xuser where LOWER(username) = LOWER(${userName}) order by created desc limit 1")(session)
  }

  def UserByEmail(email:String)(implicit session: DBSession = ReadOnlyAutoSession):Option[User] = {
    by(sql"select id, email, username, isactive, password, created, parentid  from xuser where LOWER(email) = LOWER(${email}) order by created desc limit 1")
  }

  private def by(sqlQuery:SQL[_, _])(implicit session: DBSession = ReadOnlyAutoSession):Option[User] = {
    sqlQuery
    .map { rs =>
      new User(
                maybeId = Option(rs.string("id")).map(UUID.fromString),
                maybeUserName = Option(rs.string("username")).filterNot(_.trim.isEmpty),
                email = rs.string("email"),
                hashedPassword = rs.string("password"),
                isActive = rs.boolean("isactive"),
                maybeCreated = Option(rs.jodaDateTime("created")),
                maybeParentId = Option(rs.string("parentid")).map(UUID.fromString)
              )
    }
    .single.apply()
  }

  def addUserFirstTime(user:User, created:DateTime, uUID: UUID = UUID.randomUUID())(implicit session: DBSession = AutoSession):Try[User] = {
    val result = sql"insert into xuser (id, username, email, password, isactive, parentid, created) values (${uUID}, ${user.maybeUserName}, ${user.email}, ${user.hashedPassword}, ${user.isActive}, ${uUID}, ${created})"
      .update.apply()
    if (result == 1) {
      UserByEmail(user.email).map(Success(_)).getOrElse(Failure(new RuntimeException("Problem adding user to DB.")))
    } else {
      Failure(new RuntimeException("Problem adding user to DB."))
    }
  }

}