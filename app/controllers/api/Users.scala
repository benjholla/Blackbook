package controllers.api

import controllers.traits._
import models._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._

import json.UserJson._

object Users extends Controller with SecuredApi { 
  def all() = SecuredApiCall(Permission.ViewUsers) {
    SuccessWithData(toJson(models.User.all map jsFromUser))
  }

  def get(name: String) = SecuredApiCall(Permission.ViewUsers) {
    SuccessWithData(toJson(jsFromUser(User.find(name))))
  }

  def update = SecuredApiCall(Permission.EditUsers)(parse.json)
  { body =>
    body.validate[JsUser].map { jsUser =>
      User.find(jsUser.name).update(jsUser)
      Success()
    }.recoverTotal(ValidationError)
  }
  
  def disable(name: String) = SecuredApiCall(Permission.EditUsers){
    User.disable(name)
    Success()
  }
}
