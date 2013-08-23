package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

object Tags extends Controller {

  val tagForm = Form (
    "name" -> nonEmptyText
  )
  
  def tags = Action { 
    Ok(views.html.tags.index(Tag.all(), tagForm))
  }
  
  def newTag = Action { implicit request =>
    tagForm.bindFromRequest.fold(
      errors => BadRequest(views.html.tags.index(Tag.all(), errors)),
      name => {
        Tag.create(name)
        Redirect(routes.Tags.tags)
      })
  }

  def deleteTag(id: Long) = Action {
    Tag.delete(id)
    Redirect(routes.Tags.tags)
  }
  
}
