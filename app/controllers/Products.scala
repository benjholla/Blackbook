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

object Products extends Controller with Secured {

  val productForm = Form (
    tuple("name" -> nonEmptyText,
          "description" -> nonEmptyText)
  )
  
  def products = WithPermissions(Perm.ViewProducts) 
  { implicit request => 
    Ok(views.html.products.index(Product.all()))
  }
  
  def viewProduct(id: Long) = WithPermissions(Perm.ViewProducts)
  { implicit request => 
    val product = Product.find(id)
    product match { 
      case Some(p) => Ok(views.html.products.view(p))
      case None => BadRequest(views.html.products.index(Product.all()))
    }
  }
  
  def editProduct(id: Long) = 
    WithPermissions(Perm.ViewProducts + Perm.EditProducts)
  { implicit request =>
    val product = Product.find(id)
    product match { 
      case Some(p) => Ok(views.html.products.edit(p,productForm.fill((p.name, p.description))))
      case None => throw new Exception("No product " + id + " found.")
    }
  }
  
  def updateProduct(id: Long) = 
    WithPermissions(Perm.ViewProducts + Perm.EditProducts)
  { implicit request =>
    productForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.products.edit(Product.find(id).get, formWithErrors)),
      form => {
        val (name, description) = form
        val product = Product.find(id)
	    product match { 
	      case Some(p) => {Product.update(p.id, name, description); Redirect(routes.Products.editProduct(p.id))}
	      case None => BadRequest(views.html.products.index(Product.all()))
	    }
      })
  }

  def newProduct() = 
    WithPermissions(Perm.ViewProducts + Perm.EditProducts)
  { implicit request =>
    Ok(views.html.products.newProduct(productForm))
  }
  
  def createProduct = 
    WithPermissions(Perm.ViewProducts + Perm.EditProducts)
  { implicit request =>
    productForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.products.newProduct(formWithErrors)),
      form => {
        val (name, description) = form
        val product = Product.create(name, description)
        Redirect(routes.Products.editProduct(product.id))
      })
  }

  def deleteProduct(id: Long) = 
    WithPermissions(Perm.ViewProducts + Perm.EditProducts)
  { implicit request =>
    Product.delete(id)
    Redirect(routes.Products.products)
  }
  
  
  private[this] def getProductFilePath(id: Long, filename:String):File = {
    return new File(Db.uploadsPath() + id + "/files/" + filename)
  }
  
  // This should be used only for determining where to save an icon 
  // file when one is upload, not for getting the icon file
  private[this] def getProductIconPath(id: Long):File = {
    return new File(Db.uploadsPath() + id + "/icon")
  }
  
  // This should be used for getting the icon file, 
  // this includes a default icon file if an icon is not found
  private[this] def getProductIconFile(id: Long):File = {
    var file = getProductIconPath(id)
    if(file.exists()){
      return file;
    } else {
      return Play.getFile(Db.defaultProductIconPath())
    }
  }
  
  def getIcon(id: Long) = WithPermissions(Perm.ViewProducts) 
  { implicit request => 
    Ok.sendFile(getProductIconFile(id))
  }
  
  def getFile(id: Long, filename:String) = 
    WithPermissions(Perm.ViewProducts + Perm.DownloadFile)
  { implicit request => 
    Ok.sendFile(getProductFilePath(id, filename))
  }
  
  def deleteFile(id: Long, filename:String) = 
    WithPermissions(Perm.ViewProducts + Perm.EditProducts)
  { implicit request => 
    getProductFilePath(id, filename).delete()
    Redirect(routes.Products.editProduct(id))
  }
  
  def uploadProductFile(id: Long) = 
    WithPermissions(Perm.ViewProducts + Perm.EditProducts)(parse.multipartFormData)
  { implicit request =>
	  request.body.file("fileUpload").map { fileUpload =>
	    val filename = fileUpload.filename 
	    val contentType = fileUpload.contentType
	    fileUpload.ref.moveTo(getProductFilePath(id,filename), replace=true)
	    Redirect(routes.Products.editProduct(id))
	  }.getOrElse {
	    Redirect(routes.Products.editProduct(id)).flashing("error" -> "Missing file")
	  }
  }
  
  def uploadProductIcon(id: Long) = 
    WithPermissions(Perm.ViewProducts + Perm.EditProducts)(parse.multipartFormData)
  { request => 
	  request.body.file("iconUpload").map { fileUpload =>
	    val contentType = fileUpload.contentType
	    if(contentType.get.toString().startsWith("image/")){
	      fileUpload.ref.moveTo(getProductIconPath(id), replace=true)
	      Redirect(routes.Products.editProduct(id))
	    } else {
	      getProductIconPath(id).delete();
	      Redirect(routes.Products.editProduct(id)).flashing("error" -> (contentType.get.toString() + " is not a PNG file"))
	    }
	  }.getOrElse {
	    Redirect(routes.Products.editProduct(id)).flashing("error" -> "Missing file")
	  }
  }
}
