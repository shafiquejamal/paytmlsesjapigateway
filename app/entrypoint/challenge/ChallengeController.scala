package entrypoint.challenge

import play.api.mvc._

class ChallengeController extends Controller {

  def helloWorld = Action { Ok("Hello World") }

}
