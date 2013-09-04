package controllers

import java.io.File
import models._
import models.{Permission => Perm}
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import scala.collection.mutable.ArrayBuffer
import traits._
import util.Db
import play.api.data.format.Formatter

object Announcements extends Controller with Secured {

  val announcementForm = Form (
    "message" -> nonEmptyText
  )
  
  def announcements() = WithPermissions() { implicit request => 
    Ok(views.html.messages(Announcement.all(), announcementForm))
  }

  def createAnnouncement() = WithPermissions(Perm.EditUsers) { implicit request =>
    announcementForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.messages(Announcement.all(), formWithErrors)),
      form => {
        val (message) = form
        Announcement.create(message)
        Redirect(routes.Announcements.announcements())
      })
  }
    
}