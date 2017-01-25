package asynccommunication

import access.CodeSender
import user.UserMessage

class TestCodeSender extends CodeSender {

  def send(user: UserMessage,
    code: String,
    subjectMessageKey: String,
    bodyTextMessageKey: String): Unit = {}

}
