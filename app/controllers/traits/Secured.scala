package controllers.traits

import models._
import play.api.mvc._
import play.api.mvc.BodyParsers._

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

