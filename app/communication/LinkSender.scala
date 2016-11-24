package communication

import user.UserMessage

trait LinkSender {

  def send(user: UserMessage,
      code: String,
      subjectMessageKey: String,
      bodyTextMessageKey: String): Unit

}
