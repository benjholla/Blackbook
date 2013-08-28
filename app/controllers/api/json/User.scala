package controllers.api.json

import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import scala.language.implicitConversions

import models._

object UserJson {
  case class JsUser 
    ( name: String,
      password: String,
      permissions: Long,
      enabled: Boolean
    ) extends User.User 
  {
    def hasPermission(perm: Permission.Value): Boolean =
      (permissions & perm.id) != 0
  }

  implicit val userReads = reads[JsUser]
  implicit val userWrites = writes[JsUser]

  implicit def jsFromUser(u: User.User): JsUser =
    JsUser(u.name, u.password, u.getPermissions, u.enabled)
}
