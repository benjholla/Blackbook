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

object Products extends Api { 
  /** Handle an API request to list products. */
  def all() = { 
    apiCall( Success(toJson(models.Product.all())) ) 
  }

 
  /** Handle an API request to create a new product. */
  def create = apiCall { body =>
    val maybeName = (body \ "name").validate[String]
    val desc = (body \ "description").validate[String].getOrElse("")

    maybeName.map { name =>
      Success(toJson(Product.create(name, desc)))
    }.recoverTotal {
      e => ValidationError(e) 
    }
  }

  /** Handle an API request to update a product. */
  def update = apiCall { body =>
    body.validate[Product].map { p =>
      val updated = Product.update(p.id, p.name, p.description)
      Success(toJson(updated))
    }.recoverTotal { 
      e => ValidationError(e)
    }
  }

}
