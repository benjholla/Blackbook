package controllers
import play.api._
import play.api.mvc._

object Decoy extends Controller {
  def home = Action {
    Ok(views.html.decoy.home())
  }
  
  def about = Action {
    Ok(views.html.decoy.about())
  }
  
  def humanRights = Action {
    Ok(views.html.decoy.human_rights())
  }
  
  def cleanWater = Action {
    Ok(views.html.decoy.clean_water())
  }
  
  def mobileAid = Action {
    Ok(views.html.decoy.mobile_aid())
  }
  
  def wpAdmin = Action {
    Redirect(routes.Application.login())
  }
}
