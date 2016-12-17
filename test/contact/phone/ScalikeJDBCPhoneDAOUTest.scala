package contact.phone

import contact.phone.PhoneNumberStatus.Unverified
import db.{CrauthAutoRollback, TestDBConnection, TestScalikeJDBCSessionProvider}
import org.scalatest.fixture.FlatSpec
import org.scalatest.{ShouldMatchers, _}
import scalikejdbc.DBSession
import user.UserFixture
import util.TestUUIDProviderImpl

import scala.util.Success

class ScalikeJDBCPhoneDAOUTest
  extends FlatSpec
  with ShouldMatchers
  with CrauthAutoRollback
  with UserFixture
  with BeforeAndAfterEach
  with TestDBConnection {

  "Adding a phone number" should "succeed if the status of the number to be added is unverified" in { session =>
    val dAO = makeDAO(session)

    val numberEarlier = "18883216112"
    val numberLater = "12025551212"
    val phoneNumberToAddEarlier = PhoneNumber(numberEarlier, Unverified, yesterday)
    val phoneNumberToAddLater = PhoneNumber(numberLater, Unverified, yesterday.plusMillis(1))
    dAO.addPhoneNumber(id1, phoneNumberToAddEarlier) shouldBe a[Success[_]]
    dAO.addPhoneNumber(id1, phoneNumberToAddLater) shouldBe a[Success[_]]
    dAO.phoneNumber(id1).map(_.phoneNumber) should contain(phoneNumberToAddLater)
  }

  private def makeDAO(session: DBSession) =
    new ScalikeJDBCPhoneDAO(TestScalikeJDBCSessionProvider(session), dBConfig, new TestUUIDProviderImpl())

}
