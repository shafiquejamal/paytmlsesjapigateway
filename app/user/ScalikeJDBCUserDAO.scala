package user

import java.util.UUID

import com.google.inject.{Inject, Singleton}
import db.{DBConfig, ScalikeJDBCSessionProvider}
import entity.User
import org.joda.time.DateTime
import scalikejdbc.TxBoundary.Try._
import scalikejdbc._
import util.UUIDProvider

import scala.util.{Failure, Success, Try}

@Singleton
class ScalikeJDBCUserDAO @Inject()(wrappedResultSetToUserConverter: WrappedResultSetToUserConverter,
                                   scalikeJDBCSessionProvider: ScalikeJDBCSessionProvider,
                                   dBConfig: DBConfig,
                                   uUIDProvider: UUIDProvider) extends UserDAO {

  val userFields = "xuser.id, email, username, status, password, createdat"
  val tables = "xuser inner join xuserusername on xuserusername.xuserid = xuser.id inner join xuserstatus on " +
               "xuserstatus.xuserid = xuser.id inner join xuseremail.xuserid = xuser.id inner join xuserpassword on " +
               "xuserpassword.xuserid = xuser.id"

  override def byUsername(username: String): Option[User] =
    by(
      sql"""select xuser.id, email, username, status, password, xuser.createdat from xuser inner join xuserusername on
            xuserusername.xuserid = xuser.id inner join xuserstatus on xuserstatus.xuserid = xuser.id inner join xuseremail
            on xuseremail.xuserid = xuser.id inner join xuserpassword on xuserpassword.xuserid = xuser.id where
            LOWER(username) = (${username.trim.toLowerCase()}) order by xuserstatus.createdat desc, xuserusername.createdat
            desc, xuserpassword.createdat desc, xuseremail.createdat desc, xuserusername.createdat desc limit 1""")

  override def byEmail(email: String): Option[User] =
    by(
      sql"""select xuser.id, email, username, status, password, xuser.createdat from xuser inner join xuserusername on
            xuserusername.xuserid = xuser.id inner join xuserstatus on xuserstatus.xuserid = xuser.id inner join xuseremail
            on xuseremail.xuserid = xuser.id inner join xuserpassword on xuserpassword.xuserid = xuser.id where
            LOWER(email) = (${email.trim.toLowerCase()}) order by xuserstatus.createdat desc,  xuserusername.createdat desc,
            xuserpassword.createdat desc, xuseremail.createdat desc, xuserusername.createdat desc limit 1""")

  override def by(id: UUID): Option[User] =
    by(
      sql"""select xuser.id, email, username, status, password, xuser.createdat from xuser inner join xuserusername on
            xuserusername.xuserid = xuser.id inner join xuserstatus on xuserstatus.xuserid = xuser.id inner join xuseremail
            on xuseremail.xuserid = xuser.id inner join xuserpassword on xuserpassword.xuserid = xuser.id where
            xuser.id = ${id} order by xuserstatus.createdat desc,  xuserusername.createdat desc, xuserpassword.createdat
            desc, xuseremail.createdat desc, xuserusername.createdat desc limit 1""")

  override def byEmail(email:String, hashedPassword:String): Option[User] = byEmail(email).filter(_.hashedPassword == hashedPassword)

  override def byUsername(username:String, hashedPassword:String): Option[User] = byUsername(username).filter(_.hashedPassword == hashedPassword)

  private def by(sqlQuery: SQL[_, _]): Option[User] = {
    implicit val session = scalikeJDBCSessionProvider.provideReadOnlySession
    sqlQuery.map(wrappedResultSetToUserConverter.converter).single.apply().filter(_.isActive)
  }

  override def addFirstTime(user: User, created: DateTime, uUID: UUID): Try[User] = {
    val (username, email) = (user.username.trim, user.email.trim)
    val result:Try[User] = DB localTx { _ =>

      implicit val session = scalikeJDBCSessionProvider.provideAutoSession

      val isUsernameIsAvailable = byUsername(username).isEmpty
      val isEmailAddressIsAvailable = byEmail(email).isEmpty
      val isUsernameDoesNotMatchExistingActiveUser = byEmail(username).isEmpty

      if (isUsernameIsAvailable & isEmailAddressIsAvailable & isUsernameDoesNotMatchExistingActiveUser) {
        sql"""insert into xuser (id, authorid, createdat) values (${uUID}, ${uUID}, ${created})""".update.apply()
        sql"""insert into xuseremail (id, xuserid, authorid, createdat, email) values
             (${uUIDProvider.randomUUID()}, ${uUID}, ${uUID}, ${created}, ${email})""".update.apply()
        sql"""insert into xuserstatus (id, xuserid, authorid, createdat, status) values
             (${uUIDProvider.randomUUID()}, ${uUID}, ${uUID}, ${created}, true)""".update.apply()
        sql"""insert into xuserpassword (id, xuserid, authorid, createdat, password) values
             (${uUIDProvider.randomUUID()}, ${uUID}, ${uUID}, ${created}, ${user.hashedPassword})""".update.apply()
        sql"""insert into xuserusername (id, xuserid, authorid, createdat, username) values
             (${uUIDProvider.randomUUID()}, ${uUID}, ${uUID}, ${created}, ${username})""".update.apply()

        val activeUsersWithThisUsernameOrEmail =
          (byUsername(username).toList ++ byEmail(email).toList ++ byEmail(username).toList)
          .groupBy(_.maybeId).flatMap{ case (maybeId, users) => users.headOption }.toList

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