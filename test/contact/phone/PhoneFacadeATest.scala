package contact.phone

import contact.phone.PhoneNumberStatus.Unverified
import db._
import org.scalatest.fixture.FlatSpec
import org.scalatest.{BeforeAndAfterEach, ShouldMatchers}
import scalikejdbc.DBSession
import user.{TestUserImpl, UserFixture}
import util.TestTimeProviderImpl
import org.scalatest.TryValues._

import scala.util.{Failure, Try}

class PhoneFacadeATest
  extends FlatSpec
  with ShouldMatchers
  with CrauthAutoRollback
  with UserFixture
  with BeforeAndAfterEach
  with TestDBConnection {

  val user = new TestUserImpl()
  val timeProvider = new TestTimeProviderImpl()

  "Registering a valid phone number" should "succeed" in { session =>
    val api = makeAPI(session)
    api.registerPhoneNumber(id1, PhoneNumberRegistrationMessage("18883216111")).success.value shouldEqual
      PhoneNumber("18883216111", Unverified, timeProvider.now())
  }

  "Registering an invalid phone number" should "fail" in { session =>
    val api = makeAPI(session)
    Try(api.registerPhoneNumber(id1, PhoneNumberRegistrationMessage("8883216111"))) shouldBe a[Failure[_]]
  }

  private def makeAPI(session: DBSession) = {
    val phoneDAO = new ScalikeJDBCPhoneDAOImpl(TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    new PhoneFacade(phoneDAO, timeProvider)
  }
  
}
