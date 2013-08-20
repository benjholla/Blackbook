package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

object ProductController extends Controller {

  val productForm = Form (
    "name" -> nonEmptyText
  )
  
  def products = Action {
    Ok(views.html.products(Product.all(), productForm))
  }

  def newProduct = Action { implicit request =>
    productForm.bindFromRequest.fold(
      errors => BadRequest(views.html.products(Product.all(), errors)),
      name => {
        Product.create(name)
        Redirect(routes.ProductController.products)
      })
  }

  def deleteProduct(id: Long) = Action {
    Product.delete(id)
    Redirect(routes.ProductController.products)
  }

  def tagsForProduct(id: Long) = Action { 
    val product = Product.find(id)

    product match { 
      case Some(p) => Ok(views.html.product_tags(p))
      case None => BadRequest(views.html.products(Product.all(), productForm))
    }
  }
  
}
