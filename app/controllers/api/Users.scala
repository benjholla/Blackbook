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
    SuccessWithData(toJson(models.User.all.values map jsFromUser))
  }

  def get(name: String) = SecuredApiCall(Permission.ViewUsers) {
    SuccessWithData(toJson(User.getUser(name) map jsFromUser))
  }

  def update = SecuredApiCall(Permission.EditUsers)(parse.json)
  { body =>
    body.validate[JsUser].map { jsUser =>
      User.getUser(jsUser.name) map { user =>
        Success()
      } getOrElse { ApiError("User not found") }
    }.recoverTotal(ValidationError)
  }
}
