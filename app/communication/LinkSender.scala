package communication

import user.UserMessage

trait LinkSender {

  def send(user: UserMessage,
      host: String,
      code: String,
      route: String,
      subjectMessageKey: String,
      bodyTextMessageKey: String): Unit

}
