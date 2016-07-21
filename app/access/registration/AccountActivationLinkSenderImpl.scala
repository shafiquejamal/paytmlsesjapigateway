package access.registration

import java.net.URLEncoder.encode

import com.google.inject.Inject
import communication.Emailer
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import user.{User, UserStatus}


class AccountActivationLinkSenderImpl @Inject() (
    configuration:Configuration,
    emailer:Emailer,
    override val messagesApi: MessagesApi)
  extends AccountActivationLinkSender
  with I18nSupport {

  override def sendActivationCode(user: User, protocolAndHost: String, key: String):Unit = {

    val activationCode = ActivationCodeGenerator.generate(user.maybeId.map(_.toString).getOrElse(""), key)
    val urlEncodedEmailAddress = encode(user.email, "UTF-8")
    val link = s"$protocolAndHost/#/activate?email=$urlEncodedEmailAddress&code=$activationCode"

    val from = configuration.getString("crauth.emailFrom").getOrElse("")
    val subject = Messages("activation.subject")
    val bodyText = Messages("activation.body", link)

    emailer.sendEmail(
      subject,
      from,
      Seq(s"${user.username} <${user.email}>"),
      Some(bodyText))

  }

  override val statusOnRegistration = UserStatus.Unverified

}
