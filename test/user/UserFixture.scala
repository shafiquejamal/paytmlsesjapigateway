package user

import java.util.UUID

import scalikejdbc._
import user.UserStatus._
import util.{TestTimeProviderImpl, TestUUIDProviderImpl}

trait UserFixture {

  val now = TestTimeProviderImpl.now()
  val later = now.plusDays(1)
  val id1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
  val id2 = UUID.fromString("00000000-0000-0000-0000-000000000002")
  val id3 = UUID.fromString("00000000-0000-0000-0000-000000000003")
  val id4 = UUID.fromString("00000000-0000-0000-0000-000000000004")
  val id5 = UUID.fromString("00000000-0000-0000-0000-000000000005")
  val id6 = UUID.fromString("00000000-0000-0000-0000-000000000006")

  val uUIDProvider = TestUUIDProviderImpl
  uUIDProvider.index(100)

  val alice =
    TestUserImpl(Some(id1), "alice", "alice@alice.com", "passwordAliceID2", userStatus = Active, Some(now))

  val sqlToAddUsers = Vector(
    sql"insert into xuser  (id, authorid, createdat) values (${id1}, ${id1}, ${now})",
    sql"insert into xuser  (id, authorid, createdat) values (${id2}, ${id2}, ${now})",
    sql"insert into xuser  (id, authorid, createdat) values (${id3}, ${id3}, ${now})",
    sql"insert into xuser  (id, authorid, createdat) values (${id4}, ${id4}, ${now})",
    sql"insert into xuser  (id, authorid, createdat) values (${id5}, ${id5}, ${now})",
    sql"""insert into xuserstatus  (id, authorid, createdat, xuserid, status) values
         (${uUIDProvider.randomUUID()}, ${id1}, ${now}, ${id1}, ${Active.value})""",
    sql"""insert into xuserstatus  (id, authorid, createdat, xuserid, status) values
         (${uUIDProvider.randomUUID()}, ${id3}, ${now}, ${id3}, ${Admin.value})""",
    sql"""insert into xuserstatus  (id, authorid, createdat, xuserid, status) values
         (${uUIDProvider.randomUUID()}, ${id4}, ${now}, ${id4}, ${Active.value})""",
    sql"""insert into xuserstatus  (id, authorid, createdat, xuserid, status) values
         (${uUIDProvider.randomUUID()}, ${id4}, ${later}, ${id4}, ${Unverified.value})""",
    sql"""insert into xuseremail  (id, authorid, createdat, xuserid, email) values
         (${uUIDProvider.randomUUID()}, ${id1}, ${now}, ${id1}, 'alice@alice.com')""",
    sql"""insert into xuseremail  (id, authorid, createdat, xuserid, email) values
         (${uUIDProvider.randomUUID()}, ${id3}, ${now}, ${id3}, 'bob@bob.com')""",
    sql"""insert into xuseremail  (id, authorid, createdat, xuserid, email) values
         (${uUIDProvider.randomUUID()}, ${id4}, ${now}, ${id4}, 'charlie@charlie.com')""",
    sql"""insert into xuserusername  (id, authorid, createdat, xuserid, username) values
         (${uUIDProvider.randomUUID()}, ${id1}, ${now}, ${id1}, 'alice')""",
    sql"""insert into xuserusername  (id, authorid, createdat, xuserid, username) values
         (${uUIDProvider.randomUUID()}, ${id3}, ${now}, ${id3}, 'bob')""",
    sql"""insert into xuserusername  (id, authorid, createdat, xuserid, username) values
         (${uUIDProvider.randomUUID()}, ${id4}, ${now}, ${id4}, 'charlie')""",
    sql"""insert into xuserpassword  (id, authorid, createdat, xuserid, password) values
         (${uUIDProvider.randomUUID()}, ${id1}, ${now}, ${id1}, 'passwordAliceID1')""",
    sql"""insert into xuserpassword  (id, authorid, createdat, xuserid, password) values
         (${uUIDProvider.randomUUID()}, ${id1}, ${later}, ${id1}, 'passwordAliceID2')""",
    sql"""insert into xuserpassword  (id, authorid, createdat, xuserid, password) values
         (${uUIDProvider.randomUUID()}, ${id3}, ${now}, ${id3}, 'passwordBobID3')""",
    sql"""insert into xuserpassword  (id, authorid, createdat, xuserid, password) values
         (${uUIDProvider.randomUUID()}, ${id4}, ${now}, ${id4}, 'passwordCharlieID4')""",
    sql"""insert into xuserpassword  (id, authorid, createdat, xuserid, password) values
         (${uUIDProvider.randomUUID()}, ${id4}, ${later}, ${id4}, 'passwordCharlieID5')"""
  )


}
