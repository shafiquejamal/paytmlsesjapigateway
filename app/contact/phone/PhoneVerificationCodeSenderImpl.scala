package contact.phone

import com.google.inject.Inject
import play.api.Configuration

import scala.concurrent.Future

class PhoneVerificationCodeSenderImpl @Inject()(configuration: Configuration) extends PhoneVerificationCodeSender {

  override def send(code: String, phoneNumber: PhoneNumber): Future[String] = {
    val sendLink: String = configuration.getString("crauth.sendSMSLink").getOrElse("")
    val adminPhoneNumber: String = configuration.getString("crauth.adminPhoneNumber").getOrElse("")
    val sendSMSKey: String = configuration.getString("crauth.sendSMSKey").getOrElse("")
    val textToSend = s"Your activation code is: $code"
    val url = s"$sendLink?from=$adminPhoneNumber&to=${phoneNumber.number}&mode=TEXT&text=$textToSend&key=$sendSMSKey"

    import scala.concurrent.ExecutionContext.Implicits.global
    
    Future {
      val response = scala.io.Source.fromURL(url).mkString
      if (response.isEmpty)
        throw new Exception
      else
        response
    }
  }

}
