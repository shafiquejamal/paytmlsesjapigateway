package user

import java.util.UUID

import com.google.inject.{Inject, Singleton}
import db.{DBConfig, ScalikeJDBCSessionProvider}
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

  override def byUsername(username: String, userFilter: User => Boolean): Option[User] =
    by(
      sql"""select xuser.id, email, username, status, password, xuser.createdat from xuser inner join xuserusername on
            xuserusername.xuserid = xuser.id inner join xuserstatus on xuserstatus.xuserid = xuser.id inner join xuseremail
            on xuseremail.xuserid = xuser.id inner join xuserpassword on xuserpassword.xuserid = xuser.id where
            LOWER(username) = (${username.trim.toLowerCase()}) order by xuserstatus.createdat desc, xuserusername.createdat
            desc, xuserpassword.createdat desc, xuseremail.createdat desc, xuserusername.createdat desc limit 1""",
      userFilter)

  override def byEmail(email: String, userFilter: User => Boolean): Option[User] =
    by(
      sql"""select xuser.id, email, username, status, password, xuser.createdat from xuser inner join xuserusername on
            xuserusername.xuserid = xuser.id inner join xuserstatus on xuserstatus.xuserid = xuser.id inner join xuseremail
            on xuseremail.xuserid = xuser.id inner join xuserpassword on xuserpassword.xuserid = xuser.id where
            LOWER(email) = (${email.trim.toLowerCase()}) order by xuserstatus.createdat desc,  xuserusername.createdat desc,
            xuserpassword.createdat desc, xuseremail.createdat desc, xuserusername.createdat desc limit 1""",
      userFilter)

  override def by(id: UUID, userFilter: User => Boolean): Option[User] =
    by(
      sql"""select xuser.id, email, username, status, password, xuser.createdat from xuser inner join xuserusername on
            xuserusername.xuserid = xuser.id inner join xuserstatus on xuserstatus.xuserid = xuser.id inner join xuseremail
            on xuseremail.xuserid = xuser.id inner join xuserpassword on xuserpassword.xuserid = xuser.id where
            xuser.id = ${id} order by xuserstatus.createdat desc,  xuserusername.createdat desc, xuserpassword.createdat
            desc, xuseremail.createdat desc, xuserusername.createdat desc limit 1""", userFilter)

  private def by(sqlQuery: SQL[_, _], userFilter: User => Boolean): Option[User] = {
    implicit val session = scalikeJDBCSessionProvider.provideReadOnlySession
    sqlQuery.map(wrappedResultSetToUserConverter.converter).single.apply().filter(userFilter)
  }

  override def add(
    user: User,
    created: DateTime,
    uUID: UUID,
    registrationUserFilter: User => Boolean,
    authenticationUserFilter: User => Boolean): Try[User] = {

    val (username, email) = (user.username.trim, user.email.trim)
    DB localTx { _ =>

      implicit val session = scalikeJDBCSessionProvider.provideAutoSession

      val isUsernameIsAvailable = byUsername(username, authenticationUserFilter).isEmpty
      val isEmailAddressIsAvailable = byEmail(email, authenticationUserFilter).isEmpty
      val isUsernameDoesNotMatchExistingUserEmail = byEmail(username, authenticationUserFilter).isEmpty

      if (isUsernameIsAvailable & isEmailAddressIsAvailable & isUsernameDoesNotMatchExistingUserEmail) {
        sql"""insert into xuser (id, authorid, createdat) values (${uUID}, ${uUID}, ${created})""".update.apply()
        sql"""insert into xuseremail (id, xuserid, authorid, createdat, email) values
             (${uUIDProvider.randomUUID()}, ${uUID}, ${uUID}, ${created}, ${email})""".update.apply()
        sql"""insert into xuserstatus (id, xuserid, authorid, createdat, status) values
             (${uUIDProvider.randomUUID()}, ${uUID}, ${uUID}, ${created}, ${user.userStatus.value})""".update.apply()
        sql"""insert into xuserpassword (id, xuserid, authorid, createdat, password) values
             (${uUIDProvider.randomUUID()}, ${uUID}, ${uUID}, ${created}, ${user.hashedPassword})""".update.apply()
        sql"""insert into xuserusername (id, xuserid, authorid, createdat, username) values
             (${uUIDProvider.randomUUID()}, ${uUID}, ${uUID}, ${created}, ${username})""".update.apply()

        val activeUsersWithThisUsernameOrEmail = uniqueUsers(
          byUsername(username, authenticationUserFilter).toList ++
          byEmail(email, authenticationUserFilter).toList ++
          byEmail(username, authenticationUserFilter).toList)

        activeUserCheck(activeUsersWithThisUsernameOrEmail)

      } else {
        failureResult
      }
    }
  }

  override def changePassword(id: UUID, newHashedPassword:String, created:DateTime): Try[User] = {
    DB localTx { _ =>

      implicit val session = scalikeJDBCSessionProvider.provideAutoSession

      val maybeUser = by(id, (user:User) => true)

      maybeUser.fold[Try[User]](Failure(new RuntimeException("User does not exist in DB."))){ user =>
        sql"""insert into xuserpassword (id, xuserid, authorid, createdat, password) values
          (${uUIDProvider.randomUUID()}, ${id}, ${id}, ${created}, ${newHashedPassword})""".update.apply()
        Success(user)
      }
    }

  }

  override def changeUsername(id: UUID, newUsername:String, created:DateTime, authenticationUserFilter: User => Boolean):
  Try[User] = {

    DB localTx { _ =>

      val username = newUsername.trim()
      implicit val session = scalikeJDBCSessionProvider.provideAutoSession
      val isUsernameIsAvailable = byUsername(username, authenticationUserFilter).isEmpty
      val isUsernameDoesNotMatchExistingUserEmail = byEmail(username, authenticationUserFilter).isEmpty

      if (isUsernameIsAvailable & isUsernameDoesNotMatchExistingUserEmail) {

        sql"""insert into xuserusername (id, xuserid, authorid, createdat, username) values
          (${uUIDProvider.randomUUID()}, ${id}, ${id}, ${created}, ${username})""".update.apply()

        val activeUsersWithThisUsernameOrEmail = uniqueUsers(
          byUsername(username, authenticationUserFilter).toList ++
          byEmail(username, authenticationUserFilter).toList)

        activeUserCheck(activeUsersWithThisUsernameOrEmail)
      } else {
        failureResult
      }

    }

  }

  private def activeUserCheck(activeUsers:List[User]):Try[User] =
    activeUsers match {
      case addedUser :: Nil  =>
        Success(addedUser)
      case _ =>
        failureResult
    }

  val failureResult = Failure(new RuntimeException("Username or email already exists in DB."))

  private def uniqueUsers(fetchedUsers:List[User]):List[User] =
    fetchedUsers.groupBy(_.maybeId).flatMap{ case (maybeId, users) => users.headOption }.toList

}