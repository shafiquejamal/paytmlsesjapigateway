package asynccommunication

import com.google.inject.Inject
import play.api.libs.mailer.{Email, MailerClient}

class EmailerImpl @Inject()(mailerClient: MailerClient) extends Emailer {

  override def sendEmail(
      subject:String,
      from:String,
      to:Seq[String],
      bodyText:Option[String],
      cc:Seq[String] = Seq.empty,
      bcc:Seq[String] = Seq.empty):String =
    mailerClient.send(Email(subject, from, to, bodyText, None, None, cc, bcc))


}
