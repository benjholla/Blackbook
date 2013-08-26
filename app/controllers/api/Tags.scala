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

object Tags extends Api { 
  /** Get all the tags. */
  def all() = {
    apiCall( SuccessWithData(toJson(Tag.all())) )
  }

  /** Get one tag */
  def get(id: Long) = {
    apiCall( SuccessWithData(toJson(Tag.find(id))) )
  }

  /** Get all the products associated with a tag */
  def getProducts(id: Long) = {
    apiCall( SuccessWithData(toJson(Tag.getProducts(id))) )
  }
}
