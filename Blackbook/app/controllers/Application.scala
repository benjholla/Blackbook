package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

import play.api.libs.json._
import play.api.libs.json.Json._

object Application extends Controller with Secured {

  val loginForm = Form(
    tuple( 
      "username" -> text,
      "password" -> text 
    ) verifying ( "Invalid username or password", result => result match {
      case (u, p) => User.authenticate(u, p).isDefined })
  ) 

  def index = Action {
    Redirect(routes.Application.login)
  }
  
  def login = Action { implicit request => 
    Ok(views.html.login(loginForm))
  }

  def logout = Action { 
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You have been logged out."
    )
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => Redirect(routes.Products.products).withSession("username" -> user._1)
    )
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

trait Secured {
  private def username(request: RequestHeader) = request.session.get("username")

  private def onUnauthorized(request: RequestHeader) = Results.NotFound

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = 
    Security.Authenticated(username, onUnauthorized) 
  { user =>
    Action(request => f(user)(request))
  }

  def WithPermission(perm: Permission.Value)(f: => String => Request[AnyContent] => Result) = 
    IsAuthenticated { user => request => 
    if (User.getUser(user) exists { u => u.hasPermission(perm) }) {
      f(user)(request)
    } else {
      Results.NotFound
    }
  }
}
