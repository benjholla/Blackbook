package controllers.traits

import models._
import play.api.mvc._
import play.api.mvc.BodyParsers._

trait Secured {
  protected def username(request: RequestHeader): Option[String] = 
    request.session.get("username")

  protected def onUnauthorized(request: RequestHeader) = 
    controllers.Application.notFound(request)
  
  implicit def getLoggedInUser(implicit request: RequestHeader): User.User = { 
    username(request) map { name: String => 
      User.getUser(name)
    } getOrElse(User.NullUser())
  }

  def isLoggedIn(request: RequestHeader): Boolean = 
    getLoggedInUser(request).anyPermissions

  case class WithPermissions(perms: Permission.Set = Permission.Set()) {
    def apply[A <: Any]
      (b: BodyParser[A])
      (f: => Request[A] => Result) = 
    {
      Security.Authenticated(username, onUnauthorized) 
      { auth_name => Action(b)
        { implicit request => 
          implicit val user = getLoggedInUser(request)
          if (user.hasPermissions(perms)) f(request)
          else onUnauthorized(request)
        }
      }
    }

    def apply(f: => Request[AnyContent] => Result): EssentialAction = 
      apply(parse.anyContent) { implicit request => f(request) }
  }
}

