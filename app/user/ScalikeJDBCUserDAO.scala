package user

import java.util.UUID

import access.authentication.PasswordResetCodeAndDate
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

  val namedDB = NamedDB(Symbol(dBConfig.dBName))
  namedDB.autoClose(false)
  val readOnlySession = scalikeJDBCSessionProvider.provideReadOnlySession

  override def passwordResetCode(userId: UUID): Option[PasswordResetCodeAndDate] = {
    implicit val session = readOnlySession
    sql"""select passwordresetcode, createdat, active from xuserpasswordresetcode where xuserid=$userId
            order by createdat desc limit 1"""
      .map(rs => (PasswordResetCodeAndDate.converter(rs), rs.boolean("active"))).single.apply()
      .filter(_._2).map(_._1)
  }

  override def passwordResetCode(userId: UUID, code:String): Option[PasswordResetCodeAndDate] = {
    passwordResetCode(userId: UUID).filter(_.code == code)
  }

  override def byUsername(username: String, userFilter: User => Boolean): Option[User] =
    byUsernameWithSession(username, userFilter)(readOnlySession)

  private def byUsernameWithSession(username: String, userFilter: User => Boolean)(implicit session:DBSession): Option[User] =
     sql"""select xuser.id, email, username, status, password, xuser.createdat from xuser inner join xuserusername on
            xuserusername.xuserid = xuser.id inner join xuserstatus on xuserstatus.xuserid = xuser.id inner join xuseremail
            on xuseremail.xuserid = xuser.id inner join xuserpassword on xuserpassword.xuserid = xuser.id where
            LOWER(username) = (${username.trim.toLowerCase()}) order by xuserstatus.createdat desc, xuserusername.createdat
            desc, xuserpassword.createdat desc, xuseremail.createdat desc, xuserusername.createdat desc limit 1"""
     .map(wrappedResultSetToUserConverter.converter).single.apply().filter(userFilter)

  override def byEmail(email: String, userFilter: User => Boolean): Option[User] =
    byEmailWithSession(email, userFilter)(readOnlySession)

  private def byEmailWithSession(email: String, userFilter: User => Boolean)(implicit session:DBSession): Option[User] =
    sql"""select xuser.id, email, username, status, password, xuser.createdat from xuser inner join xuserusername on
            xuserusername.xuserid = xuser.id inner join xuserstatus on xuserstatus.xuserid = xuser.id inner join xuseremail
            on xuseremail.xuserid = xuser.id inner join xuserpassword on xuserpassword.xuserid = xuser.id where
            LOWER(email) = (${email.trim.toLowerCase()}) order by xuserstatus.createdat desc,  xuserusername.createdat desc,
            xuserpassword.createdat desc, xuseremail.createdat desc, xuserusername.createdat desc limit 1"""
        .map(wrappedResultSetToUserConverter.converter).single.apply().filter(userFilter)

  override def by(id: UUID, userFilter: User => Boolean): Option[User] = {
    implicit val session = readOnlySession
    byWithSession(id, userFilter)
  }

  private def isUserIsValid(xuserId: UUID, statusOfUserFilter: StatusOfUser => Boolean)(implicit session:DBSession): Boolean =
    sql"""select xuserid, status, createdat from xuserstatus where xuserid = $xuserId order by createdat desc limit 1"""
        .map(wrappedResultSetToUserConverter.toStatusOfUser).single().apply().exists(statusOfUserFilter)

  private def isTokenIsLatest(xuserId: UUID, iat: DateTime)
      (implicit session:DBSession): Boolean = {
    sql"""select xuserid, iat from xuseriatsingleusetoken where xuserid = $xuserId
         order by xuseriatsingleusetoken.iat desc limit 1"""
    .map(wrappedResultSetToUserConverter.toSingleUseToken)
    .single().apply().forall(_.iat.isBefore(iat))
  }

  override def validateOneTime(
      xuserId: UUID,
      iat: DateTime,
      statusOfUserFilter: StatusField => Boolean,
      createdat: DateTime,
      uUID: UUID): Option[User] = {

    implicit val session = scalikeJDBCSessionProvider.provideAutoSession
    val userIsValid = isUserIsValid(xuserId, statusOfUserFilter)
    val tokenIsLatest = isTokenIsLatest(xuserId, iat)

    if (userIsValid && tokenIsLatest) {
      val isTokenAdded =
        sql"""insert into xuseriatsingleusetoken (id, authorid, xuserid, iat, createdat) values
             (${uUID}, ${xuserId}, ${xuserId}, ${iat}, ${createdat})""".update.apply() == 1
      if (isTokenAdded) {
        by(xuserId, statusOfUserFilter)
      } else {
        None
      }
    } else {
      None
    }

  }

  private def byWithSession(id: UUID, userFilter: User => Boolean)(implicit session:DBSession): Option[User] = {
    sql"""select xuser.id, email, username, status, password, xuser.createdat from xuser inner join xuserusername on
      xuserusername.xuserid = xuser.id inner join xuserstatus on xuserstatus.xuserid = xuser.id inner join xuseremail
      on xuseremail.xuserid = xuser.id inner join xuserpassword on xuserpassword.xuserid = xuser.id where
      xuser.id = ${id} order by xuserstatus.createdat desc,  xuserusername.createdat desc, xuserpassword.createdat
      desc, xuseremail.createdat desc, xuserusername.createdat desc limit 1"""
    .map(wrappedResultSetToUserConverter.converter).single.apply().filter(userFilter)
  }

  override def add(
    user: User,
    created: DateTime,
    uUID: UUID,
    userFilter: User => Boolean): Try[User] = {

    val (username, email) = (user.username.trim, user.email.trim)
    namedDB localTx { _ =>

      implicit val session = scalikeJDBCSessionProvider.provideAutoSession

      val isUsernameIsAvailable = byUsernameWithSession(username, userFilter).isEmpty
      val isEmailAddressIsAvailable = byEmailWithSession(email, userFilter).isEmpty
      val isUsernameDoesNotMatchExistingUserEmail = byEmailWithSession(username, userFilter).isEmpty

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
          byUsernameWithSession(username, userFilter).toList ++
          byEmailWithSession(email, userFilter).toList ++
          byEmailWithSession(username, userFilter).toList)

        userCheck(activeUsersWithThisUsernameOrEmail)

      } else {
        Failure(new RuntimeException("username or email address is not available, or username is an email address that " +
                                     "does not match the given email address."))
      }
    }
  }

  override def changePassword(id: UUID, newHashedPassword:String, created:DateTime): Try[User] =
    namedDB localTx { _ =>

      implicit val session = scalikeJDBCSessionProvider.provideAutoSession
      val maybeUser = byWithSession(id, (user:User) => true)

      maybeUser.fold[Try[User]](Failure(new RuntimeException("User does not exist in DB."))){ user =>
        sql"""insert into xuserpassword (id, xuserid, authorid, createdat, password) values
          (${uUIDProvider.randomUUID()}, ${id}, ${id}, ${created}, ${newHashedPassword})""".update.apply()
        confirmUpdate(id)
      }
    }

  override def addStatus(id: UUID, userStatus:UserStatus, created:DateTime): Try[User] = {
    namedDB localTx { _ =>

      implicit val session = scalikeJDBCSessionProvider.provideAutoSession
      val maybeUser = byWithSession(id, (user:User) => true)

      maybeUser.fold[Try[User]](Failure(new RuntimeException("User does not exist in DB."))){ user =>
        sql"""insert into xuserstatus (id, xuserid, authorid, createdat, status) values
          (${uUIDProvider.randomUUID()}, ${id}, ${id}, ${created}, ${userStatus.value})""".update.apply()
        confirmUpdate(id)
      }
    }

  }

  override def addPasswordResetCode(userId: UUID, passwordResetCode:String, created:DateTime, active:Boolean): Try[User] = {
    namedDB localTx { _ =>

      implicit val session = scalikeJDBCSessionProvider.provideAutoSession
      val maybeUser = byWithSession(userId, (user:User) => true)

      maybeUser.fold[Try[User]](Failure(new RuntimeException("User does not exist in DB."))){ user =>
        sql"""insert into xuserpasswordresetcode (id, xuserid, authorid, createdat, passwordresetcode, active) values
          (${uUIDProvider.randomUUID()}, ${userId}, ${userId}, ${created}, ${passwordResetCode}, ${active})""".update.apply()
        confirmUpdate(userId)
                                                                                            }
    }

  }

  private def confirmUpdate(id:UUID)(implicit session:DBSession):Try[User] =
    byWithSession(id, (user:User) => true).fold[Try[User]](Failure(new RuntimeException("Could not update user")))(user =>
      Success(user))

  override def changeUsername(id: UUID, newUsername:String, created:DateTime, authenticationUserFilter: User => Boolean):
  Try[User] = {

    namedDB localTx { _ =>

      val username = newUsername.trim()
      implicit val session = scalikeJDBCSessionProvider.provideAutoSession
      val isUsernameIsAvailable = byUsernameWithSession(username, authenticationUserFilter).isEmpty
      val isUsernameDoesNotMatchExistingUserEmail = byEmailWithSession(username, authenticationUserFilter).isEmpty

      if (isUsernameIsAvailable & isUsernameDoesNotMatchExistingUserEmail) {
        sql"""insert into xuserusername (id, xuserid, authorid, createdat, username) values
          (${uUIDProvider.randomUUID()}, ${id}, ${id}, ${created}, ${username})""".update.apply()

        val usersWithThisUsernameOrEmail = uniqueUsers(
          byUsername(username, authenticationUserFilter).toList ++
          byEmail(username, authenticationUserFilter).toList)

        userCheck(usersWithThisUsernameOrEmail)
      } else {
        failureResult
      }
    }

  }

  private def userCheck(activeUsers:List[User]):Try[User] =
    activeUsers match {
      case addedUser :: Nil  =>
        Success(addedUser)
      case Nil =>
        Failure(new RuntimeException("Could not add user to the db."))
      case _ =>
        Failure(new RuntimeException("After trying to add the user, the username or email address is no longer unique."))
    }

  val failureResult = Failure(new RuntimeException("Username or email already exists in DB."))

  private def uniqueUsers(fetchedUsers:List[User]):List[User] =
    fetchedUsers.groupBy(_.maybeId).flatMap{ case (maybeId, users) => users.headOption }.toList

  override def allLogoutDate(id: UUID):Option[DateTime] = {
    implicit val session = readOnlySession
    sql"""select alllogoutdate from xuseralllogoutdate where xuserid = ${id} order by alllogoutdate desc limit 1"""
    .map { _.jodaDateTime("alllogoutdate") }.single.apply()
  }

  override def addAllLogoutDate(id: UUID, allLogoutDate: DateTime, created:DateTime):Try[User] =
    namedDB localTx { _ =>

      implicit val session = scalikeJDBCSessionProvider.provideAutoSession
      val maybeUser = byWithSession(id, (user:User) => true)

      maybeUser.fold[Try[User]](Failure(new RuntimeException("User does not exist in DB."))){ user =>
        sql"""insert into xuseralllogoutdate (id, xuserid, authorid, createdat, alllogoutdate) values
          (${uUIDProvider.randomUUID()}, ${id}, ${id}, ${created}, ${allLogoutDate})""".update.apply()
        confirmUpdate(id)}
    }


}