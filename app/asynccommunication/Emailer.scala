package asynccommunication

trait Emailer {

  def sendEmail(
      subject:String,
      from:String,
      to:Seq[String],
      bodyText:Option[String],
      cc:Seq[String] = Seq.empty,
      bcc:Seq[String] = Seq.empty):String
}
