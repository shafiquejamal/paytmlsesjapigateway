package contact

import db.{CrauthAutoRollback, TestDBConnection, TestScalikeJDBCSessionProvider}
import org.scalatest.LoneElement._
import org.scalatest.TryValues._
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.DBSession
import user.UserFixture
import util.TestUUIDProviderImpl

class ContactDAOUTest
  extends FlatSpec
  with ShouldMatchers
  with CrauthAutoRollback
  with UserFixture
  with BeforeAndAfterEach
  with TestDBConnection {

  "Retrieving visible contacts for a user" should "show only the contacts that should be visible" in { session =>
    val dAO = makeDAO(session)

    dAO.visibleContactsFor(id1).loneElement shouldEqual Contact(id3, "bob")
    dAO.visibleContactsFor(id3) should contain theSameElementsAs Seq(Contact(id1, "alice"), Contact(id7, "diane"))
    dAO.visibleContactsFor(id4).loneElement shouldEqual Contact(id7, "diane")
  }

  "Adding a contact" should "make an invisible contact visible" in { session =>
    val dAO = makeDAO(session)

    dAO.addContact(id1, id4, now.plusMillis(1)).success.value shouldEqual id4
    dAO.visibleContactsFor(id1) should contain theSameElementsAs Seq(Contact(id3, "bob"), Contact(id4, "charlie"))
  }

  it should "make a non-connected contact connected and visible" in { session =>
    val dAO = makeDAO(session)

    dAO.addContact(id3, id4, now.plusMillis(1)).success.value shouldEqual id4
    dAO.visibleContactsFor(id3) should contain theSameElementsAs Seq(
      Contact(id1, "alice"), Contact(id7, "diane"), Contact(id4, "charlie"))

  }

  private def makeDAO(session: DBSession) =
    new ContactDAOImpl(TestScalikeJDBCSessionProvider(session), dBConfig, new TestUUIDProviderImpl())

}
