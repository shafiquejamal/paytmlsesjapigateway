package user

import java.util.UUID

import com.google.inject.Inject
import db.ScalikeJDBCSessionProvider
import entity.User
import org.joda.time.DateTime
import scalikejdbc._

import scala.util.{Failure, Success, Try}

class ScalikeJDBCUserDAO @Inject() (wrappedResultSetToUserConverter: WrappedResultSetToUserConverter,
                                    scalikeJDBCSessionProvider:ScalikeJDBCSessionProvider) extends UserDAO {

  val readOnlySession = scalikeJDBCSessionProvider.provideReadOnlySession

  override def addFirstTime(user:User, created:DateTime, uUID: UUID):Try[User] =
    addUserFirstTime(user:User, created, uUID)

  override def byUserName(userName:String):Option[User] =
    by(sql"select id, email, username, isactive, password, created, parentid from xuser where LOWER(username) = LOWER(${userName}) order by created desc limit 1")

  override def byEmail(email:String):Option[User] =
    by(sql"select id, email, username, isactive, password, created, parentid from xuser where LOWER(email) = LOWER(${email}) order by created desc limit 1")

  override def byParentID(parentID:UUID):Option[User] =
    by(sql"select id, email, username, isactive, password, created, parentid from xuser where parentid = ${parentID} order by created desc limit 1")

  private def by(sqlQuery:SQL[_, _]):Option[User] = {
    implicit val session = readOnlySession
    sqlQuery.map(wrappedResultSetToUserConverter.converter).single.apply()
  }

  def addUserFirstTime(user:User, created:DateTime, uUID: UUID = UUID.randomUUID()):Try[User] = {
    val result = sql"insert into xuser (id, username, email, password, isactive, parentid, created) values (${uUID}, ${user.maybeUserName}, ${user.email}, ${user.hashedPassword}, ${user.isActive}, ${uUID}, ${created})"
      .update.apply()(scalikeJDBCSessionProvider.provideAutoSession)
    if (result == 1) {
      byEmail(user.email).map(Success(_)).getOrElse(Failure(new RuntimeException("Problem adding user to DB.")))
    } else {
      Failure(new RuntimeException("Problem adding user to DB."))
    }
  }

}