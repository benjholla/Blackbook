package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

object Application extends Controller {

  val productForm = Form (
    "label" -> nonEmptyText
  )

  val tagForm = Form (
    "label" -> nonEmptyText
  )

  def index = Action {
    Redirect(routes.Application.products())
  }

  def products = Action {
    Ok(views.html.products(Product.all(), productForm))
  }

  def tags = Action { 
    Ok(views.html.tags(Tag.all(), tagForm))
  }

  def newProduct = Action { implicit request =>
    productForm.bindFromRequest.fold(
      errors => BadRequest(views.html.products(Product.all(), errors)),
      label => {
        Product.create(label)
        Redirect(routes.Application.products)
      })
  }

  def deleteProduct(id: Long) = Action {
    Product.delete(id)
    Redirect(routes.Application.products)
  }

  def newTag = Action { implicit request =>
    tagForm.bindFromRequest.fold(
      errors => BadRequest(views.html.tags(Tag.all(), errors)),
      label => {
        Tag.create(label)
        Redirect(routes.Application.tags)
      })
  }

  def deleteTag(id: Long) = Action {
    Tag.delete(id)
    Redirect(routes.Application.tags)
  }
}
