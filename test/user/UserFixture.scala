package user

import java.util.UUID

import scalikejdbc._
import util.TestTimeProviderImpl

trait UserFixture {

  val now = TestTimeProviderImpl.now()
  val later = now.plusDays(1)
  val id1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
  val id2 = UUID.fromString("00000000-0000-0000-0000-000000000002")
  val id3 = UUID.fromString("00000000-0000-0000-0000-000000000003")
  val id4 = UUID.fromString("00000000-0000-0000-0000-000000000004")
  val id5 = UUID.fromString("00000000-0000-0000-0000-000000000005")
  val id6 = UUID.fromString("00000000-0000-0000-0000-000000000006")

  val alice2 =
    TestUserImpl(Some(id2), "alice", "alice@alice.com", "passwordAliceID2", isActive = true, Some(later), Some(id1))

  val sqlToAddUsers = Vector(
    sql"insert into xuser  (id, username, email, password, isactive, created) values (${id1}, 'alice', 'alice@alice.com', 'passwordAliceID1', true, ${now})",
    sql"insert into xuser  (id, username, email, password, isactive, created, parentid) values (${id2}, 'alice', 'alice@alice.com', 'passwordAliceID2', true, ${later}, ${id1})",
    sql"insert into xuser  (id, username, email, password, isactive, created) values (${id3}, 'bob', 'bob@bob.com', 'passwordBobID3', true, ${now})",
    sql"insert into xuser  (id, username, email, password, isactive, created) values (${id4}, 'charlie', 'charlie@charlie.com', 'passwordCharlieID4', true, ${now})",
    sql"insert into xuser  (id, username, email, password, isactive, created, parentid) values (${id5}, 'charlie', 'charlie@charlie.com', 'passwordCharlieID5', false, ${later}, ${id4})"
  )


}
