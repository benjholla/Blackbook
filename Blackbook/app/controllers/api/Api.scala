package controllers.api

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._

import models._

class Api extends Controller {
  // JSON serializers for products
  implicit val productReads = reads[Product]
  implicit val productWrites = writes[Product]
  
  // JSON serializers for tags
  implicit val tagReads = reads[Tag]
  implicit val tagWrites = writes[Tag]

  abstract class ApiResult
  case class Success(result: JsValue) extends ApiResult
  case class ParseError(e: String) extends ApiResult
  case class ValidationError(e: JsError) extends ApiResult

  /* Get the status string for the given API result. */
  def statusString(result: ApiResult): String = { 
    result match {
      case Success(_) => "OK"
      case _ => "error"
    }
  }

  /* Get the Result constructor for the given ApiResult. */
  def toResult(result: ApiResult) = {
    result match {
      case Success(_) => Ok
      case _ => BadRequest
    }
  }

  /* Get the JSON content of the response for the given ApiResult. */
  def content(result: ApiResult): (String, JsValue) = {
    result match {
      case Success(v) => "result" -> v
      case ParseError(msg) => "message" -> toJson(msg)
      case ValidationError(e) => "message" -> JsError.toFlatJson(e)
    }
  }

  def apiResponse(api_result: ApiResult): Result = {
    val result = toResult(api_result)
    result(toJson(Map(
      "status" -> toJson(statusString(api_result)),
      content(api_result)
    )))
  }

  /* Wraps one of our API functions, invoking it with the user-provided JsResult
   * and converting the ApiResult to a Result */
  def apiCall(api_result: ApiResult) = Action { _ =>
    apiResponse(api_result)
  }

  /* Wraps one of our API functions, invoking it with the user-provided JsResult
   * and converting the ApiResult to a Result */
  def apiCall(call: JsValue => ApiResult) = Action(parse.json) { request =>
    apiResponse(call(request.body))
  }

}

