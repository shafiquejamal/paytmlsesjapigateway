package communication

import com.google.inject.Inject
import play.api.Configuration
import play.api.libs.mailer.{Email, MailerClient}

class TestEmailerImpl @Inject() (mailerClient: MailerClient, configuration:Configuration) extends Emailer {

  override def sendEmail(
      subject:String,
      from:String,
      to:Seq[String],
      bodyText:Option[String],
      cc:Seq[String] = Seq.empty,
      bcc:Seq[String] = Seq.empty):String =
    mailerClient
    .send(
      Email(
        subject,
        from,
        Seq(configuration.getString("accessService.testEmailRecipient").getOrElse("")),
        bodyText,
        None,
        None,
        cc,
        bcc))

}
