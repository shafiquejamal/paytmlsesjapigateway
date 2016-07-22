package communication

import user.User

class TestLinkSender extends LinkSender {

  def send(user: User,
    host: String,
    code: String,
    route: String,
    subjectMessageKey: String,
    bodyTextMessageKey: String): Unit = {}


}
