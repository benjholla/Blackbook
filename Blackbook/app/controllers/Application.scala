package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

object Application extends Controller {

  def index = TODO
  
  def login = Action {
    Ok(views.html.login())
  }
  
  def order = Action {
    Ok(views.html.order())
  }

  def users = TODO

  def javascriptRoutes = Action { implicit request =>
    import controllers.api.routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        Products.all,
        Products.create,
        Products.update,
        Products.updateMany,
        Products.delete,
        Products.get,
        Products.getTags,
        Products.addTags,
        Products.removeTags,
        Tags.all, 
        Tags.get,
        Tags.getProducts
      )
    ).as("text/javascript")
  }
  
}
