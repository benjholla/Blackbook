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
    Ok(views.html.products.index(Product.all(), productForm))
  }
  
  def viewProduct(id: Long) = Action {
    val product = Product.find(id)
    product match { 
      case Some(p) => Ok(views.html.products.view(p))
      case None => BadRequest(views.html.products.index(Product.all(), productForm))
    }
  }
  
  def editProduct(id: Long) = Action {
    val product = Product.find(id)
    product match { 
      case Some(p) => Ok(views.html.products.edit(p, productForm))
      case None => BadRequest(views.html.products.index(Product.all(), productForm))
    }
  }
  
  def updateProduct(id: Long) = Action { implicit request =>
    productForm.bindFromRequest.fold(
      errors => BadRequest(views.html.products.index(Product.all(), errors)),
      name => {
        val product = Product.find(id)
	    product match { 
	      case Some(p) => {Product.update(p.id, name); Redirect(routes.ProductController.products)}
	      case None => BadRequest(views.html.products.index(Product.all(), productForm))
	    }
      })
  }

  def newProduct = Action { implicit request =>
    productForm.bindFromRequest.fold(
      errors => BadRequest(views.html.products.index(Product.all(), errors)),
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
      case None => BadRequest(views.html.products.index(Product.all(), productForm))
    }
  }
  
//  def viewProduct(id: Long) = Action {
//    val product = Product.find(id)
//    Ok(product.)
//  }
  
}
