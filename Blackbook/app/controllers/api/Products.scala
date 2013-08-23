package controllers.api

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._

import models._

object Products extends Controller { 
  implicit val productWrites = Json.writes[Product]
  implicit val productReads = Json.reads[Product]
  
  implicit val tagWrites = Json.writes[Tag]
  implicit val tagReads = Json.reads[Tag]
  
  def products = Action { 
    Ok(toJson(Map(
      "status" -> toJson("OK"),
      "result" -> toJson(models.Product.all())
    )))
  }

}
