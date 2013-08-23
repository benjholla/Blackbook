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

object Products extends Controller { 
  implicit val productReads = reads[Product]
  implicit val productWrites = writes[Product]
  
  implicit val tagReads = reads[Tag]
  implicit val tagWrites = writes[Tag]
  
  def all = Action { 
    Ok(toJson(Map(
      "status" -> toJson("OK"),
      "result" -> toJson(models.Product.all())
    )))
  }
  
  def create = Action(parse.json) { request =>
    ( request.body \ "name" ).asOpt[String].map { name =>
      val result = models.Product.create(name)
      Ok(toJson(Map(
        "status" -> toJson("OK"),
        "result" -> toJson(result)
      )))
    }.getOrElse { 
      BadRequest("Missing parameter [name]")
    }
  }

}
