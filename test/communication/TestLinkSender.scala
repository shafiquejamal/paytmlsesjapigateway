package communication

import user.UserMessage

class TestLinkSender extends LinkSender {

  def send(user: UserMessage,
    code: String,
    subjectMessageKey: String,
    bodyTextMessageKey: String): Unit = {}

}
