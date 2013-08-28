package controllers.api

import play.api._
import play.api.mvc._
import play.api.mvc.BodyParsers._
import play.api.data._
import play.api.libs.json._
import play.api.libs.json.Json.toJson
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._
import play.api.mvc.Results._

import models._

trait Api {
  abstract class ApiResult
  case class Success() extends ApiResult
  case class SuccessWithData(data: JsValue) extends ApiResult
  case class ApiError(msg: String) extends ApiResult
  case class ValidationError(e: JsError) extends ApiResult

  /* Get the status string for the given API result. */
  def statusString(result: ApiResult): String = { 
    result match {
      case Success() => "OK"
      case SuccessWithData(_) => "OK"
      case ValidationError(_) => "validationError"
      case ApiError(_) => "apiError"
      case _ => "error"
    }
  }

  /* Get the Result constructor for the given ApiResult. */
  def toResult(result: ApiResult) = {
    result match {
      case Success() => Ok
      case SuccessWithData(_) => Ok
      case _ => BadRequest
    }
  }

  /* Get the JSON content of the response for the given ApiResult. */
  def content(result: ApiResult): Option[(String, JsValue)] = {
    result match {
      case SuccessWithData(v) => Some("result" -> v)
      case ValidationError(e) => Some("message" -> JsError.toFlatJson(e))
      case ApiError(msg) => Some("message" -> toJson(msg))
      case _ => None
    }
  }

  def apiResponse(api_result: ApiResult): Result = {
    val result = toResult(api_result)
    var fields = Map("status" -> toJson(statusString(api_result)))
    var maybeContent = content(api_result)
    
    if (maybeContent.isDefined) { 
      fields += maybeContent.get
    }

    result(toJson(fields))
  }

  object ApiCall
  {
    def apply[A <: Any](b: BodyParser[A])(f: => A => ApiResult) = Action(b) 
    { request => apiResponse(f(request.body)) }

    def apply(f: => ApiResult) = Action
    { apiResponse(f) }
  }
}

trait SecuredApi extends Api with controllers.traits.Secured {
  case class SecuredApiCall(perms: Permission.Set = Permission.Set()) {
    def apply[A <: Any](b: BodyParser[A])(f: => A => ApiResult) = 
    { 
      Security.Authenticated(username, onUnauthorized)
      { auth_name => Action(b) 
        { request => 
          val user = getLoggedInUser(request)
          if (user.hasPermissions(perms)) apiResponse(f(request.body))
          else onUnauthorized(request)
        }
      }
    }

    def apply(f: => ApiResult): EssentialAction = 
     apply(parse.anyContent) { content => f }
  }
}
