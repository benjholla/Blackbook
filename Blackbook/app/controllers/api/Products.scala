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
  /** Handle an API request to list products. */
  def all() = { 
    apiCall( Success(toJson(models.Product.all())) ) 
  }

 
  /** Handle an API request to create a new product. */
  /*def create = Action(parse.json) = {  request =>
    request.body.validate[JsObject].map { obj =>
      ( obj \ "name" ).asOpt[String].map { name =>
        val result = models.Product.create(name)
        Ok(toJson(Map(
          "status" -> toJson("OK"),
          "result" -> toJson(result)
        )))
      }.getOrElse {
        BadRequest(toJson(Map(
          "status" -> "error",
          "error" -> "Missing parameter [name]"
        )))
      }
    }.recoverTotal { 
      e => BadRequest(toJson(Map(
        "status" -> toJson("error")
  }
      
  
      obj => 
      case obj => {
      }
    }
  }
    ( request.body \ "name" ).asOpt[String].map { name =>
      val result = models.Product.create(name)
      Ok(toJson(Map(
        "status" -> toJson("OK"),
        "result" -> toJson(result)
      )))
    }.recoverTotal {
      e => BadRequest("Error: " + JsError.toFlatJson(e))
    }
  }*/

  /** Handle an API request to update a product. */
  /*def update = Action(parse.json) { request =>
    request.body.validate[Product].map { 
      Product.update(p.id, p.name, p.description)
      Ok(toJson(Map("status" -> "OK")))
    }.recoverTotal { 
      e => BadRequest("Error: " + JsError.toFlatJson(e))
    }
  }*/

}
