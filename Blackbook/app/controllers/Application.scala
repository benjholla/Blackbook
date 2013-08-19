package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Product

object Application extends Controller {

  val productForm = Form (
    "label" -> nonEmptyText
  )

  def index = Action {
    Redirect(routes.Application.products())
  }

  def products = Action {
    Ok(views.html.products(Product.all(), productForm))
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

}
