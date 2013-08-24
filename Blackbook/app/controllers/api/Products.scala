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
    apiCall( SuccessWithData(toJson(models.Product.all())) ) 
  }
 
  /** Handle an API request to create a new product. */
  def create = apiCall { body =>
    val created = for {
      name <- (body \ "name").validate[String]
      desc <- (body \ "description").validate[String] orElse JsSuccess("")
    } yield toJson(Product.create(name, desc))

    created.map(SuccessWithData).recoverTotal(ValidationError)
  }

  /** Handle an API request to update a product. */
  def update = apiCall { body =>
    body.validate[Product].map { p =>
      val updated = Product.update(p.id, p.name, p.description)
      SuccessWithData(toJson(updated))
    }.recoverTotal(ValidationError)
  }

  /** Handle an API request to delete a product. */
  def delete = apiCall { body =>
    (body \ "id").validate[Long].map { id =>
      Product.delete(id)
      Success()
    }.recoverTotal(ValidationError)
  }

  def get(id: Long) = {
    apiCall( SuccessWithData(toJson(Product.find(id))) )
  }
}
