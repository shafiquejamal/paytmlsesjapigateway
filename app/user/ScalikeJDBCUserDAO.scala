package user

import java.util.UUID

import com.google.inject.Inject
import db.{DBConfig, ScalikeJDBCSessionProvider}
import entity.User
import org.joda.time.DateTime
import scalikejdbc.TxBoundary.Try._
import scalikejdbc._

import scala.util.{Failure, Success, Try}

class ScalikeJDBCUserDAO @Inject()(wrappedResultSetToUserConverter: WrappedResultSetToUserConverter,
                                   scalikeJDBCSessionProvider: ScalikeJDBCSessionProvider,
                                   dBConfig: DBConfig) extends UserDAO {

  dBConfig.setUpAllDB()

  override def byUsername(username: String): Option[User] =
    by(sql"select id, email, username, isactive, password, created, parentid from xuser where LOWER(username) = LOWER(${username}) order by created desc limit 1")

  override def byEmail(email: String): Option[User] =
    by(sql"select id, email, username, isactive, password, created, parentid from xuser where LOWER(email) = LOWER(${email}) order by created desc limit 1")

  override def by(parentID: UUID): Option[User] =
    by(sql"select id, email, username, isactive, password, created, parentid from xuser where parentid = ${parentID} order by created desc limit 1")

  override def byEmail(email:String, hashedPassword:String): Option[User] = byEmail(email).filter(_.hashedPassword == hashedPassword)

  override def byUsername(username:String, hashedPassword:String): Option[User] = byUsername(username).filter(_.hashedPassword == hashedPassword)

  private def by(sqlQuery: SQL[_, _]): Option[User] = {
    implicit val session = scalikeJDBCSessionProvider.provideReadOnlySession
    sqlQuery.map(wrappedResultSetToUserConverter.converter).single.apply().filter(_.isActive)
  }

  override def addFirstTime(user: User, created: DateTime, uUID: UUID): Try[User] = {
    val (username, email) = (user.username, user.email)
    val result:Try[User] = DB localTx { _ =>

      implicit val session = scalikeJDBCSessionProvider.provideAutoSession

      val isUsernameIsAvailable =
        !sql"select id, email, username, isactive, password, created, parentid from xuser where LOWER(username) = LOWER(${username}) order by created desc limit 1"
        .map(wrappedResultSetToUserConverter.converter).list().apply().headOption.exists(_.isActive)
      val isEmailAddressIsAvailable =
        !sql"select id, email, username, isactive, password, created, parentid from xuser where LOWER(email) = LOWER(${email}) order by created desc limit 1"
        .map(wrappedResultSetToUserConverter.converter).list().apply().headOption.exists(_.isActive)

      if (isUsernameIsAvailable & isEmailAddressIsAvailable) {
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