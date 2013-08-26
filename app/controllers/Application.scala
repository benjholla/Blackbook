package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.BodyParsers._
import play.api.data._
import play.api.data.Forms._
import models._

object Application extends Controller with Secured {

  val loginForm = Form(
    tuple( 
      "username" -> text,
      "password" -> text 
    ) verifying ( "Invalid username or password", result => result match {
      case (u, p) => User.authenticate(u, p).isDefined })
  ) 

  def transactions = TODO
  
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
  
  def login = Action { implicit request => 
    Ok(views.html.login(loginForm))
  }

  def logout = Action { 
    Redirect(routes.Application.home).withNewSession
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
        c.Products.viewProduct,
        c.Products.editProduct,
        c.Products.getIcon
      )
    ).as("text/javascript")
  }
  
}

trait Secured {
  private def username(request: RequestHeader) = request.session.get("username")

  private def onUnauthorized(request: RequestHeader) = 
    if (isLoggedIn(request)) Results.Unauthorized
    else Results.NotFound

  private def IsAuthenticatedBase[A]
    (b: BodyParser[A] = parse.anyContent)
    (f: => User.User => Request[A] => Result) = 
  {
    Security.Authenticated(username, onUnauthorized) 
    { auth_name => Action(b) { 
        request => getLoggedInUser(request) map { 
          user => f(user)(request) 
        } getOrElse { 
          onUnauthorized(request)
        }
      }
    }
  }

  def IsAuthenticated[A <: Any]
    (b: BodyParser[A])
    (f: => User.User => Request[A] => Result) = 
    IsAuthenticatedBase[A](b)(f)
  def IsAuthenticated(f: => User.User => Request[AnyContent] => Result) = 
    IsAuthenticatedBase[AnyContent](parse.anyContent)(f)

  def getLoggedInUser(request: RequestHeader): Option[User.User] =
    username(request) match { 
      case Some(user) => User.getUser(user)
      case None => None
    }

  def isLoggedIn(request: RequestHeader): Boolean = 
    getLoggedInUser(request).isDefined
    
  private def WithPredicate[A <: Any]
    (b: BodyParser[A] = parse.anyContent)
    (pred: User.User => Boolean)
    (f: => User.User => Request[A] => Result) = IsAuthenticated[A](b)
  { user => request => 
    if (pred(user)) {
      f(user)(request)
    } else {
      onUnauthorized(request)
    }
  }

  private def WithPermissionsBase[A <: Any]
    (b: BodyParser[A] = parse.anyContent)
    (perms: Permission.Set)
    (f: => User.User => Request[A] => Result) = WithPredicate[A](b)
  { user => user.hasPermissions(perms) }
  { user => request => 
    f(user)(request)
  }

  case class WithPermissions[A <: Any](perms: Permission.Set) {
    def ParseWith(b: BodyParser[A])
      (f: => User.User => Request[A] => Result): EssentialAction = 
        WithPermissionsBase[A](b)(perms)(f)

    def apply(f: => User.User => Request[AnyContent] => Result): EssentialAction = 
      WithPermissionsBase[AnyContent](parse.anyContent)(perms)(f)
  }

}
