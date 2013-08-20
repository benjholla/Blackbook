package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

object ProductController extends Controller {

  val productForm = Form (
    "label" -> nonEmptyText
  )
  
  def products = Action {
    Ok(views.html.products(Product.all(), productForm))
  }

  def newProduct = Action { implicit request =>
    productForm.bindFromRequest.fold(
      errors => BadRequest(views.html.products(Product.all(), errors)),
      label => {
        Product.create(label)
        Redirect(routes.ProductController.products)
      })
  }

  def deleteProduct(id: Long) = Action {
    Product.delete(id)
    Redirect(routes.ProductController.products)
  }
  
}