package controllers.api.json

import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._

import models._

object TagJson {
  implicit val tagReads = reads[Tag]
  implicit val tagWrites = writes[Tag]
}
