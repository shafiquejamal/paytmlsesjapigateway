package access.registration

import com.google.inject.Inject
import communication.Emailer
import play.api.Configuration
import user.{UserStatus, User}
import java.net.URLEncoder.encode

class AccountActivatorImpl @Inject() (configuration:Configuration, emailer:Emailer) extends AccountActivator {

  override def sendActivationCode(user: User, protocolAndHost:String, key:String):Unit = {
    val activationCode = ActivationCodeGenerator.generate(user.maybeId.map(_.toString).getOrElse(""), key)

    val urlEncodedEmailAddress = encode(user.email, "UTF-8")

    val link = s"$protocolAndHost/activate?email=$urlEncodedEmailAddress&code=$activationCode"

    emailer.sendEmail(
      "Please verify your email to continue",
      "Admin <admin@eigenroute.com>",
      Seq(s"${user.username} <shafique.jamal@gmail.com>"), // change this!!!!
      Some(s"Please click the following link to activate your account at foo.com:\n\n$link"))

  }

  override val statusOnRegistration = UserStatus.Unverified

}
