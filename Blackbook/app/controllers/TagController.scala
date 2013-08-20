package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

object TagController extends Controller {

  val tagForm = Form (
    "name" -> nonEmptyText
  )
  
  def tags = Action { 
    Ok(views.html.tags(Tag.all(), tagForm))
  }
  
  def newTag = Action { implicit request =>
    tagForm.bindFromRequest.fold(
      errors => BadRequest(views.html.tags(Tag.all(), errors)),
      name => {
        Tag.create(name)
        Redirect(routes.TagController.tags)
      })
  }

  def deleteTag(id: Long) = Action {
    Tag.delete(id)
    Redirect(routes.TagController.tags)
  }
  
}