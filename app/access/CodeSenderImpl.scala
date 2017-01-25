package access

import com.google.inject.Inject
import communication.Emailer
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import user.UserMessage

class CodeSenderImpl @Inject()(
    emailer: Emailer,
    configuration:Configuration,
    override val messagesApi: MessagesApi)
  extends CodeSender
          with I18nSupport {

  def send(
      user:UserMessage,
      code:String,
      subjectMessageKey:String,
      bodyTextMessageKey:String):Unit = {

    val from = configuration.getString("accessService.emailFrom").getOrElse("")
    val subject = Messages(subjectMessageKey)
    val bodyText = Messages(bodyTextMessageKey:String, code)

    emailer.sendEmail(
      subject,
      from,
      Seq(s"${user.username} <${user.email}>"),
      Some(bodyText))

  }
  
}
