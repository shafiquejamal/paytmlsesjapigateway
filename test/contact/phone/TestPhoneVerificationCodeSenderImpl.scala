package contact.phone

import scala.concurrent.Future

class TestPhoneVerificationCodeSenderImpl extends PhoneVerificationCodeSender {

  import scala.concurrent.ExecutionContext.Implicits.global

  var sendWasCalled: Boolean = false

  def send(code: String, phoneNumber: PhoneNumber): Future[String] = {
    sendWasCalled = true
    Future("")
  }

}
