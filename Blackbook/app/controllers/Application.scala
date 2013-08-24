package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

object Application extends Controller {

  def index = TODO
  
  def login = Action {
    Ok(views.html.login(title="Blackbook | Login"))
  }
  
}
