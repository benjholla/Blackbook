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
      User.find(name)
    } getOrElse(User.NullUser())
  }

  def isLoggedIn(request: RequestHeader): Boolean = 
    getLoggedInUser(request).anyPermissions

  class WithUserPredicate(pred: => User.User => Boolean) {
    def apply[A <: Any]
      (b: BodyParser[A])
      (f: => Request[A] => Result) = 
    {
      Security.Authenticated(username, onUnauthorized) 
      { auth_name => Action(b)
        { implicit request => 
          implicit val user = getLoggedInUser(request)
          if (pred(user)) f(request)
          else onUnauthorized(request)
        }
      }
    }
    
    def apply(f: => Request[AnyContent] => Result): EssentialAction = 
      apply(parse.anyContent) { implicit request => f(request) }
  }

  case class WithPermissions(perms: Permission.Set = Permission.Set.empty)
    extends WithUserPredicate({ user => user.hasPermissions(perms) })

  case class WithSomePermission(perms: Permission.Set = Permission.values) 
    extends WithUserPredicate({ user => user.hasSomePermission(perms) })

  def WithPermission(perms: Permission.Set = Permission.Set.empty) =
    WithPermissions(perms)

  def WithSomePermissions(perms: Permission.Set = Permission.values) = 
    WithSomePermission(perms)
}

