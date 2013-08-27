package controllers.api.json

import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._

import models._

object ProductJson {
  implicit val productReads = reads[Product]
  implicit val productWrites = writes[Product]
}
