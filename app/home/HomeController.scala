package home

import javax.inject._

import controllers.WebJarAssets
import org.webjars.play.RequireJS
import play.api.mvc._

@Singleton
class HomeController @Inject() (
    webJarAssets: WebJarAssets,
    requireJS: RequireJS)
  extends Controller {

  def index = Action {
    Ok(views.html.index(webJarAssets, requireJS))
  }

}
