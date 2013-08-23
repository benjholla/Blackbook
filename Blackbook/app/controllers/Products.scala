package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

object Products extends Controller {

  val productForm = Form (
    tuple("name" -> nonEmptyText,
    "description" -> nonEmptyText)
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
      case Some(p) => Ok(views.html.products.edit(p,productForm.fill((p.name, p.description))))
      case None => throw new Exception("No product " + id + " found.")
    }
  }
  
  def updateProduct(id: Long) = Action { implicit request =>
    productForm.bindFromRequest.fold(
      errors => BadRequest(views.html.products.index(Product.all(), errors)),
      form => {
        val product = Product.find(id)
	    product match { 
	      case Some(p) => {Product.update(p.id, form); Redirect(routes.Products.products)}
	      case None => BadRequest(views.html.products.index(Product.all(), productForm))
	    }
      })
  }

  def newProduct = Action { implicit request =>
    productForm.bindFromRequest.fold(
      errors => BadRequest(views.html.products.index(Product.all(), errors)),
      form => {
        Product.create(form._1, form._2)
        Redirect(routes.Products.products)
      })
  }

  def deleteProduct(id: Long) = Action {
    Product.delete(id)
    Redirect(routes.Products.products)
  }

  def tagsForProduct(id: Long) = Action { 
    val product = Product.find(id)

    product match { 
      case Some(p) => Ok(views.html.product_tags(p))
      case None => BadRequest(views.html.products.index(Product.all(), productForm))
    }
  }
  
}
