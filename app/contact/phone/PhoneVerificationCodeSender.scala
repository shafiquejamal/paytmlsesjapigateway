package contact.phone

import scala.concurrent.Future

trait PhoneVerificationCodeSender {

  def send(code: String, phoneNumber: PhoneNumber): Future[String]

}
