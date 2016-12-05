package contact.phone

import contact.phone.PhoneNumberStatus.Unverified
import db.{CrauthAutoRollback, TestDBConnection, TestScalikeJDBCSessionProvider}
import org.joda.time.DateTime
import org.scalatest.TryValues._
import org.scalatest.fixture.FlatSpec
import org.scalatest.{ShouldMatchers, _}
import scalikejdbc.DBSession
import user.UserFixture
import util.TestUUIDProviderImpl

class ScalikeJDBCPhoneDAOUTest
  extends FlatSpec
  with ShouldMatchers
  with CrauthAutoRollback
  with UserFixture
  with BeforeAndAfterEach
  with TestDBConnection {

  "Adding a phone number" should "succeed if the status of the number to be added is unverified" in { session =>
    val dAO = makeDAO(session)

    val number = "18883216111"
    val phoneNumberToAddEarlier = PhoneNumber(number, Unverified, new DateTime(2016, 1, 1, 0, 0, 0))
    val phoneNumberToAddLater = PhoneNumber(number, Unverified, new DateTime(2016, 1, 2, 0, 0, 0))
    dAO.addPhoneNumber(id1, phoneNumberToAddEarlier).success.value shouldEqual phoneNumberToAddEarlier
    dAO.addPhoneNumber(id1, phoneNumberToAddLater).success.value shouldEqual phoneNumberToAddLater
    dAO.phoneNumber(id1, number) should contain(phoneNumberToAddLater)
  }

  private def makeDAO(session: DBSession) =
    new ScalikeJDBCPhoneDAOImpl(TestScalikeJDBCSessionProvider(session), dBConfig, new TestUUIDProviderImpl())

}
