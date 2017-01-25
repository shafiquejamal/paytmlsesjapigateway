package access

import user.UserMessage

trait CodeSender {

  def send(user: UserMessage,
      code: String,
      subjectMessageKey: String,
      bodyTextMessageKey: String): Unit

}
