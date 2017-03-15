package domain.twittersearch

import domain.CommonFixture
import scalikejdbc._

trait Fixture extends CommonFixture {

  val sqlToExecute = Vector(
    sql"insert into xuser  (id, authorid, createdat) values (${id1}, ${id1}, ${now})",
    sql"insert into xuser  (id, authorid, createdat) values (${id2}, ${id2}, ${now})",
    sql"""INSERT INTO searchterm (id, xuserid, searchText, createdat) values (
      $id1, $id1, 'first search text user 1', $yesterday
    )""",
    sql"""INSERT INTO searchterm (id, xuserid, searchText, createdat) values (
      $id2, $id1, 'second search text user 1', $now
    )""",
    sql"""INSERT INTO searchterm (id, xuserid, searchText, createdat) values (
      $id3, $id2, 'first search text user 2', $now
    )"""

  )

}
