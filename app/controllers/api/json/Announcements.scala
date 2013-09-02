package controllers.api

import controllers.traits._
import models._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._

import json.UserJson._

object Announcements extends Controller with SecuredApi { 
  def disable(id: Long) = SecuredApiCall(Permission.MakeAnnouncements){
    Announcement.disable(id)
    Success()
  }
}
