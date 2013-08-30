package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import java.util.Date

import models._
import traits._

object Application extends Controller with Secured {
  val loginForm = Form(
    tuple( 
      "username" -> text,
      "password" -> text 
    ) verifying ( "Invalid username or password", result => result match {
      case (u, p) => User.authenticate(u, p).anyPermissions })
  ) 
  
  def notFound(request: RequestHeader): Result = {
    val template = 
      if (isLoggedIn(request)) { views.html.not_found() } 
      else { views.html.decoy.not_found() }

    NotFound(template)
  }
  
  def error(request: RequestHeader): Result = {
    val template = 
      if (isLoggedIn(request)) { views.html.error() } 
      else { views.html.decoy.not_found() }

    NotFound(template)
  }

  def login = Action { implicit request => 
    Ok(views.html.login(loginForm))
  }

  def logout = Action { 
    Redirect(routes.Decoy.home).withNewSession
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => Redirect(routes.Application.messages).withSession("username" -> user._1)
    )
  }

  def messages = WithPermissions() 
  { implicit request => 
    Ok(views.html.messages(List[(Date, String)](new Date() -> "Welcome")))
  }

  def order = WithPermissions(Permission.ViewProducts)
  { implicit request =>
	  Ok(views.html.order())
  }

  def javascriptRoutes = WithSomePermissions()
  { implicit request =>
    import controllers.api.routes.{javascript => capi}
    import controllers.routes.{javascript => c}
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        capi.Products.all,
        capi.Products.create,
        capi.Products.update,
        capi.Products.updateMany,
        capi.Products.delete,
        capi.Products.get,
        capi.Products.getTags,
        capi.Products.addTags,
        capi.Products.removeTags,
        capi.Tags.all, 
        capi.Tags.get,
        capi.Tags.getProducts,
        capi.Users.all,
        capi.Users.get,
        capi.Users.update,
        c.Products.viewProduct,
        c.Products.editProduct,
        c.Products.getIcon,
        c.Users.viewUser
      )
    ).as("text/javascript")
  }
  
}

