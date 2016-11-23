package communication

import com.google.inject.Inject
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import user.UserMessage

class LinkSenderImpl @Inject()(
    emailer: Emailer,
    configuration:Configuration,
    override val messagesApi: MessagesApi)
  extends LinkSender
  with I18nSupport {

  def send(
      user:UserMessage,
      host:String,
      code:String,
      route:String,
      subjectMessageKey:String,
      bodyTextMessageKey:String):Unit = {

    val from = configuration.getString("crauth.emailFrom").getOrElse("")
    val subject = Messages(subjectMessageKey)
    val bodyText = Messages(bodyTextMessageKey:String, code)

    emailer.sendEmail(
      subject,
      from,
      Seq(s"${user.username} <${user.email}>"),
      Some(bodyText))

  }
  
}
