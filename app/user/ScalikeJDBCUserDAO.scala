package user

import java.util.UUID

import com.google.inject.Inject
import db.ScalikeJDBCSessionProvider
import entity.User
import org.joda.time.DateTime
import scalikejdbc.TxBoundary.Try._
import scalikejdbc._

import scala.util.{Failure, Success, Try}

class ScalikeJDBCUserDAO @Inject()(wrappedResultSetToUserConverter: WrappedResultSetToUserConverter,
                                   scalikeJDBCSessionProvider: ScalikeJDBCSessionProvider) extends UserDAO {

  override def addFirstTime(user: User, created: DateTime, uUID: UUID): Try[User] = {
    addUserFirstTime(user: User, created, uUID)
  }

  override def byUserName(userName: String): Option[User] = {
    by(sql"select id, email, username, isactive, password, created, parentid from xuser where LOWER(username) = LOWER(${userName}) order by created desc limit 1")
  }

  override def byEmail(email: String): Option[User] = {
    by(sql"select id, email, username, isactive, password, created, parentid from xuser where LOWER(email) = LOWER(${email}) order by created desc limit 1")
  }

  override def byParentID(parentID: UUID): Option[User] = {
    by(sql"select id, email, username, isactive, password, created, parentid from xuser where parentid = ${parentID} order by created desc limit 1")
  }

  private def by(sqlQuery: SQL[_, _]): Option[User] = {
    implicit val session = scalikeJDBCSessionProvider.provideReadOnlySession
    sqlQuery.map(wrappedResultSetToUserConverter.converter).single.apply()
  }

  def addUserFirstTime(user: User, created: DateTime, uUID: UUID = UUID.randomUUID()): Try[User] = {
    val (username, email) = (user.userName, user.email)
    val result:Try[User] = DB localTx { _ =>

      implicit val session = scalikeJDBCSessionProvider.provideAutoSession

      val isUserNameIsAvailable =
        !sql"select id, email, username, isactive, password, created, parentid from xuser where LOWER(username) = LOWER(${username}) order by created desc limit 1"
        .map(wrappedResultSetToUserConverter.converter).list().apply().headOption.exists(_.isActive)
      val isEmailAddressIsAvailable =
        !sql"select id, email, username, isactive, password, created, parentid from xuser where LOWER(email) = LOWER(${email}) order by created desc limit 1"
        .map(wrappedResultSetToUserConverter.converter).list().apply().headOption.exists(_.isActive)

      if (isUserNameIsAvailable & isEmailAddressIsAvailable) {
        sql"insert into xuser (id, username, email, password, isactive, parentid, created) values (${uUID}, ${username}, ${user.email}, ${user.hashedPassword}, true, ${uUID}, ${created})"
        .update.apply()

        val activeUsersWithThisUsernameOrEmail = sql"select id, email, username, isactive, password, created, parentid from xuser where LOWER(username) = LOWER(${username}) or LOWER(email) = LOWER(${email}) order by created desc limit 2"
                    .map(wrappedResultSetToUserConverter.converter).list.apply().filter(_.isActive)

        activeUsersWithThisUsernameOrEmail match {
          case addedUser :: Nil  =>
            Success(addedUser)
          case u :: otherUsers =>
            Failure(new RuntimeException("Username or email already exists in DB."))
          case _ =>
            Failure(new RuntimeException("Username or email already exists in DB."))
        }

      } else {
        Failure(new RuntimeException("Username or email already exists in DB."))
      }
    }
    result
  }

}