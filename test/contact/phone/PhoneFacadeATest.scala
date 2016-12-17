package contact.phone

import java.io.File

import com.typesafe.config.ConfigFactory
import contact.phone.PhoneNumberStatus.Unverified
import db._
import org.scalatest.fixture.FlatSpec
import org.scalatest.{BeforeAndAfterEach, ShouldMatchers}
import play.api.Configuration
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
  val configuration =
    new Configuration(ConfigFactory.parseFile(new File("conf/application.test.conf")).resolve())
  val testPhoneVerificationCodeSender = new TestPhoneVerificationCodeSenderImpl()

  val phoneNumberToReg = "18883216111"

  "Registering a valid phone number" should "succeed" in { session =>
    testPhoneVerificationCodeSender.sendWasCalled = false
    val api = makeAPI(session)
    api.registerPhoneNumber(id1, PhoneNumberRegistrationMessage(phoneNumberToReg)).success.value shouldEqual
      PhoneNumber(phoneNumberToReg, Unverified, timeProvider.now())
    testPhoneVerificationCodeSender.sendWasCalled shouldBe true
  }

  "Registering an invalid phone number" should "fail" in { session =>
    testPhoneVerificationCodeSender.sendWasCalled = false
    val api = makeAPI(session)
    Try(api.registerPhoneNumber(id1, PhoneNumberRegistrationMessage("8883216111"))) shouldBe a[Failure[_]]
    testPhoneVerificationCodeSender.sendWasCalled shouldBe false
  }

  "Re-registering the same phone number" should "not add to the db but instead just resend the activation " +
  "code" in { session =>
    uUIDProvider.index = 400
    val api = makeAPI(session)
    api.registerPhoneNumber(id1, PhoneNumberRegistrationMessage(phoneNumberToReg)).success.value shouldEqual
      PhoneNumber(phoneNumberToReg, Unverified, timeProvider.now())

    val phoneDAO = new ScalikeJDBCPhoneDAO(TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    val registerdPhoneNumberId = phoneDAO.phoneNumber(id1).map(_.id)

    testPhoneVerificationCodeSender.sendWasCalled = false
    api.registerPhoneNumber(id1, PhoneNumberRegistrationMessage(phoneNumberToReg)).success.value shouldEqual
      PhoneNumber(phoneNumberToReg, Unverified, timeProvider.now())

    phoneDAO.phoneNumber(id1).map(_.id) shouldEqual registerdPhoneNumberId
    testPhoneVerificationCodeSender.sendWasCalled shouldBe true
  }


  private def makeAPI(session: DBSession) = {
    val phoneDAO = new ScalikeJDBCPhoneDAO(TestScalikeJDBCSessionProvider(session), dBConfig, uUIDProvider)
    new PhoneFacade(phoneDAO, timeProvider, testPhoneVerificationCodeSender, configuration)
  }
  
}
