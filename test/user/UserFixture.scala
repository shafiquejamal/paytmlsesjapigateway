package user

import java.util.UUID
import scalikejdbc._

import org.joda.time.DateTime

trait UserFixture {

  val now = DateTime.now
  val later = now.plusDays(1)
  val id1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
  val id2 = UUID.fromString("00000000-0000-0000-0000-000000000002")
  val id3 = UUID.fromString("00000000-0000-0000-0000-000000000003")
  val id4 = UUID.fromString("00000000-0000-0000-0000-000000000004")
  val id5 = UUID.fromString("00000000-0000-0000-0000-000000000005")


  val sqlToAddUsers = Vector(
    sql"insert into xuser  (id, username, email, password, isactive, created) values (${id1}, 'alice', 'alice@alice.com', 'password', true, ${now})",
    sql"insert into xuser  (id, username, email, password, isactive, created, parentid) values (${id2}, 'alice', 'alice@alice.com', 'password', true, ${later}, ${id1})",
    sql"insert into xuser  (id, username, email, password, isactive, created) values (${id3}, 'bob', 'bob@bob.com', 'password', true, ${now})",
    sql"insert into xuser  (id, username, email, password, isactive, created) values (${id4}, 'charlie', 'charlie@charlie.com', 'password', true, ${now})",
    sql"insert into xuser  (id, username, email, password, isactive, created, parentid) values (${id5}, 'charlie', 'charlie@charlie.com', 'password', false, ${later}, ${id4})"

  )


}
