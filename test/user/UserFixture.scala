package user

import java.util.UUID

import scalikejdbc._
import user.UserStatus._
import util.Password.hash
import util.{TestTimeProviderImpl, TestUUIDProviderImpl}

trait UserFixture {

  val now = new TestTimeProviderImpl().now()
  val later = now.plusDays(1)
  val yesterday = now.minusDays(1).plusMillis(1)
  val dayBeforeYesterday = now.minusDays(2)

  val id1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
  val id2 = UUID.fromString("00000000-0000-0000-0000-000000000002")
  val id3 = UUID.fromString("00000000-0000-0000-0000-000000000003")
  val id4 = UUID.fromString("00000000-0000-0000-0000-000000000004")
  val id5 = UUID.fromString("00000000-0000-0000-0000-000000000005")
  val id6 = UUID.fromString("00000000-0000-0000-0000-000000000006")
  val id7 = UUID.fromString("00000000-0000-0000-0000-000000000007")
  val idNonExistentUser = UUID.fromString("90000000-0000-0000-0000-000000000000")

  val idMsgAliceBob1 = UUID.fromString("00000000-0000-0000-0000-100000000013")
  val idMsgAliceBob2 = UUID.fromString("00000000-0000-0000-0000-200000000013")
  val idMsgAliceBob3 = UUID.fromString("00000000-0000-0000-0000-300000000013")
  val idMsgBobAlice1 = UUID.fromString("00000000-0000-0000-0000-100000000031")
  val idMsgBobAlice2 = UUID.fromString("00000000-0000-0000-0000-200000000031")
  val idMsgBobAlice3 = UUID.fromString("00000000-0000-0000-0000-300000000031")

  val idContactAliceBob = UUID.fromString("00000000-0000-0000-0000-100000000013")
  val idContactBobCharlie = UUID.fromString("00000000-0000-0000-0000-100000000034")
  val idContactBobAlice = UUID.fromString("00000000-0000-0000-0000-100000000031")
  val idContactBobDiane = UUID.fromString("00000000-0000-0000-0000-100000000037")
  val idContactAliceCharlie = UUID.fromString("00000000-0000-0000-0000-100000000014")
  val idContactCharlieDiane = UUID.fromString("00000000-0000-0000-0000-100000000017")
  val idContactDianeAlice = UUID.fromString("00000000-0000-0000-0000-100000000071")

  val uUIDProvider = new TestUUIDProviderImpl()
  uUIDProvider.index = 100

  val pAlice1 = hash("passwordAliceID1")
  val pAlice2 = hash("passwordAliceID2")
  val pBob3 = hash("passwordBobID3")
  val pCharlie4 = hash("passwordCharlieID4")
  val pCharlie5 = hash("passwordCharlieID5")
  val pDiane7 = hash("passwordDianeID7")

  val passwordResetCodeAlice1 = "some password reset code for alice earlier"
  val passwordResetCodeAlice2 = "some password reset code for alice later"
  val inactivePasswordResetCodeBob1 = "some inactive password reset code for bob"

  val bobRawPhoneNumber = "18883216111"
  val aliceRawPhoneNumber = "18883216113"

  val alice =
    TestUserImpl(Some(id1), "alice", "alice@alice.com", pAlice2, userStatus = Active, Some(now))

  val charlie =
    TestUserImpl(Some(id4), "charlie", "charlie@charlie.com", pCharlie5, userStatus = Unverified, Some(now))

  val sqlToExecute = Vector(
    sql"insert into xuser  (id, authorid, createdat) values (${id1}, ${id1}, ${now})",
    sql"insert into xuser  (id, authorid, createdat) values (${id2}, ${id2}, ${now})",
    sql"insert into xuser  (id, authorid, createdat) values (${id3}, ${id3}, ${now})",
    sql"insert into xuser  (id, authorid, createdat) values (${id4}, ${id4}, ${now})",
    sql"insert into xuser  (id, authorid, createdat) values (${id5}, ${id5}, ${now})",
    sql"insert into xuser  (id, authorid, createdat) values (${id7}, ${id7}, ${now})",

    sql"""insert into xuserstatus  (id, authorid, createdat, xuserid, status) values
         (${uUIDProvider.randomUUID()}, ${id1}, ${now}, ${id1}, ${Active.value})""",
    sql"""insert into xuserstatus  (id, authorid, createdat, xuserid, status) values
         (${uUIDProvider.randomUUID()}, ${id3}, ${now}, ${id3}, ${Admin.value})""",
    sql"""insert into xuserstatus  (id, authorid, createdat, xuserid, status) values
         (${uUIDProvider.randomUUID()}, ${id4}, ${dayBeforeYesterday}, ${id4}, ${Active.value})""",
    sql"""insert into xuserstatus  (id, authorid, createdat, xuserid, status) values
         (${uUIDProvider.randomUUID()}, ${id4}, ${yesterday}, ${id4}, ${Unverified.value})""",
    sql"""insert into xuserstatus  (id, authorid, createdat, xuserid, status) values
         (${uUIDProvider.randomUUID()}, ${id7}, ${yesterday}, ${id7}, ${Blocked.value})""",

    sql"""insert into xuseremail  (id, authorid, createdat, xuserid, email) values
         (${uUIDProvider.randomUUID()}, ${id1}, ${now}, ${id1}, 'alice@alice.com')""",
    sql"""insert into xuseremail  (id, authorid, createdat, xuserid, email) values
         (${uUIDProvider.randomUUID()}, ${id3}, ${now}, ${id3}, 'bob@bob.com')""",
    sql"""insert into xuseremail  (id, authorid, createdat, xuserid, email) values
         (${uUIDProvider.randomUUID()}, ${id4}, ${now}, ${id4}, 'charlie@charlie.com')""",
    sql"""insert into xuseremail  (id, authorid, createdat, xuserid, email) values
         (${uUIDProvider.randomUUID()}, ${id7}, ${now}, ${id7}, 'diane@diane.com')""",

    sql"""insert into xuserusername  (id, authorid, createdat, xuserid, username) values
         (${uUIDProvider.randomUUID()}, ${id1}, ${now}, ${id1}, 'alice')""",
    sql"""insert into xuserusername  (id, authorid, createdat, xuserid, username) values
         (${uUIDProvider.randomUUID()}, ${id3}, ${now}, ${id3}, 'bob')""",
    sql"""insert into xuserusername  (id, authorid, createdat, xuserid, username) values
         (${uUIDProvider.randomUUID()}, ${id4}, ${now}, ${id4}, 'charlie')""",
    sql"""insert into xuserusername  (id, authorid, createdat, xuserid, username) values
         (${uUIDProvider.randomUUID()}, ${id7}, ${now}, ${id7}, 'diane')""",

    sql"""insert into xuserpassword  (id, authorid, createdat, xuserid, password) values
         (${uUIDProvider.randomUUID()}, ${id1}, ${now.minusMillis(2)}, ${id1}, ${pAlice1}) """,
    sql"""insert into xuserpassword  (id, authorid, createdat, xuserid, password) values
         (${uUIDProvider.randomUUID()}, ${id1}, ${now.minusMillis(1)}, ${id1}, ${pAlice2}) """,
    sql"""insert into xuserpassword  (id, authorid, createdat, xuserid, password) values
         (${uUIDProvider.randomUUID()}, ${id3}, ${now}, ${id3}, ${pBob3}) """,
    sql"""insert into xuserpassword  (id, authorid, createdat, xuserid, password) values
         (${uUIDProvider.randomUUID()}, ${id4}, ${dayBeforeYesterday}, ${id4}, ${pCharlie4}) """,
    sql"""insert into xuserpassword  (id, authorid, createdat, xuserid, password) values
         (${uUIDProvider.randomUUID()}, ${id4}, ${yesterday}, ${id4}, ${pCharlie5}) """,
    sql"""insert into xuserpassword  (id, authorid, createdat, xuserid, password) values
         (${uUIDProvider.randomUUID()}, ${id7}, ${yesterday}, ${id7}, ${pDiane7}) """,

    sql"""insert into xuserpasswordresetcode  (id, authorid, createdat, xuserid, passwordresetcode, active) values
         (${uUIDProvider.randomUUID()}, ${id3}, ${yesterday}, ${id3}, ${inactivePasswordResetCodeBob1}, false) """,
    sql"""insert into xuserpasswordresetcode  (id, authorid, createdat, xuserid, passwordresetcode, active) values
         (${uUIDProvider.randomUUID()}, ${id1}, ${yesterday}, ${id1}, ${passwordResetCodeAlice1}, true) """,
    sql"""insert into xuserpasswordresetcode  (id, authorid, createdat, xuserid, passwordresetcode, active) values
         (${uUIDProvider.randomUUID()}, ${id1}, ${yesterday.plusMillis(1)}, ${id1}, ${passwordResetCodeAlice2}, true) """,

    sql"""insert into xuseralllogoutdate (id, authorid, createdat, xuserid, alllogoutdate) values
         (${uUIDProvider.randomUUID()}, ${id3}, ${yesterday.plusMillis(1)}, ${id3}, ${yesterday})""",
    sql"""insert into xuseralllogoutdate (id, authorid, createdat, xuserid, alllogoutdate) values
         (${uUIDProvider.randomUUID()}, ${id3}, ${yesterday}, ${id3}, ${yesterday.plusMillis(1)})""",

    sql"""insert into xuseriatsingleusetoken (id, authorid, xuserid, createdat, iat) values
         (${uUIDProvider.randomUUID()}, ${id4}, ${id4}, ${now}, ${yesterday})""",
    sql"""insert into xuseriatsingleusetoken (id, authorid, xuserid, createdat, iat) values
         (${uUIDProvider.randomUUID()}, ${id3}, ${id3}, ${now}, ${dayBeforeYesterday})""",
    sql"""insert into xuseriatsingleusetoken (id, authorid, xuserid, createdat, iat) values
         (${uUIDProvider.randomUUID()}, ${id3}, ${id3}, ${now}, ${yesterday})"""
  )


}
