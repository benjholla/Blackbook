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
  def update(id: Long) = apiCall { body => 
    val updated = for {
      bid <- (body \ "id").validate[Long] orElse JsSuccess(id)
      name <- (body \ "name").validate[String]
      desc <- (body \ "description").validate[String]
    } yield {
      if (bid == id) {
        SuccessWithData(toJson(Product.update(id, name, desc)))
      } else {
        ApiError("Request and body IDs do not match.")
      }
    }

    updated.recoverTotal(ValidationError)
  }

  /** Handle an API request to update a list of products. */
  def updateMany = apiCall { body =>
    body.validate[List[Product]].map { plist =>
      val updated = plist map { p =>
        Product.update(p.id, p.name, p.description)
      }
      SuccessWithData(toJson(updated))
    }.recoverTotal(ValidationError)
  }

  /** Handle an API request to delete a product. */
  def delete(id: Long) = {
    Product.delete(id)
    apiCall(Success())
  }
  
  /** Get a single product. */
  def get(id: Long) = {
    apiCall( SuccessWithData(toJson(Product.find(id))) )
  }

  /** Get all the tags for a product. */
  def getTags(id: Long) = {
    apiCall( SuccessWithData(toJson(Product.getTags(id))) )
  }

  /** Add tags to a product. */
  def addTags(id: Long) = apiCall { body =>
    body.validate[List[String]].map { tags =>
      tags.map { tag =>
        Product.addTag(id, tag)
      }
      Success()
    }.recoverTotal(ValidationError)
  }

  /** Remove tags from a product. */
  def removeTags(id: Long) = apiCall { body =>
    body.validate[List[String]].map { tags =>
      tags.map { tag =>
        Product.removeTag(id, tag)
      }
      Success()
    }.recoverTotal(ValidationError)
  }
}
