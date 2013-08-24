package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import scala.language.postfixOps
import util.Db
import java.util.Date
import java.io.File
import scala.collection.mutable.ArrayBuffer
import controllers.Assets

case class Product(id: Long, name: String, description:String=null) {
  def getTags() = { Product.getTags(id) }
  def addTag(tagName: String) = { Product.addTag(id, tagName) }
  def removeTag(tagName: String) = { Product.removeTag(id, tagName) }
  def createdAt():Date = { Product.getCreatedAt(id).get }
  def lastModified():Date = { Product.getLastModified(id).get }
  def getFiles():List[File] = {
    var files = ArrayBuffer[File]()
    for(file <- new File("/tmp/products/" + id + "/files/").listFiles()){
      files += file
    }
    return files.toList
  }
  def getIcon():String = {
    if(new File("/tmp/products/" + id + "/icon.png").exists()){
      "/products/" + id + "/icon.png" 
    } else {
      "/assets/images/default-product-icon.png"
    }
  }
}

object Product {

  // Parses a product from a SQL result set
  val product = {
    get[Long]("Products.Id") ~
      get[String]("Products.Name") ~
        get[String]("Products.Description")  map {
          case id ~ name ~ description => Product(id, name, description)
        }
  }

  def all(): List[Product] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Products").as(product *)
  }

  def create(name: String, description:String): Product = {
    DB.withConnection { implicit c =>
      SQL("INSERT INTO Products(Name,Description) VALUES ({name},{description})").on(
        'name -> Db.normalizeName(name), 'description -> description).executeUpdate()
      return Product(Db.scopeIdentity(), Db.normalizeName(name), description)
    }
  }
  
  def update(id: Long, name:String, description:String): Product = {
    DB.withConnection { implicit c =>
      SQL("UPDATE Products SET Name={name}, Description={description} WHERE Id={id}").on(
        'name -> Db.normalizeName(name), 'description -> description, 'id -> id).executeUpdate()
      return Product(id, name, description)
    }
  }

  def delete(id: Long) {
    DB.withConnection { implicit c =>
      SQL("DELETE FROM PRODUCTS WHERE ID = {id}").on(
        'id -> id).executeUpdate()
    }
  }

  def find(id: Long): Option[Product] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Products WHERE Id = {id}").on(
      'id -> id).as(product *).headOption
  }
  
  def find(name: String): Option[Product] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Products WHERE Name = {name}").on(
      'name -> Db.normalizeName(name)).as(product *).headOption
  }
  
  def getCreatedAt(id: Long): Option[Date] = DB.withConnection { implicit c =>
    val selectCreatedAt = SQL("SELECT CreatedAt FROM Products WHERE Id = {id}").on('id -> id)
    val createdAt = selectCreatedAt().map(row => row[Date]("CreatedAt")).headOption
    return createdAt
  }
  
  def getLastModified(id: Long): Option[Date] = DB.withConnection { implicit c =>
    val selectLastModified = SQL("SELECT LastModified FROM Products WHERE Id = {id}").on('id -> id)
    val lastModified = selectLastModified().map(row => row[Date]("LastModified")).headOption
    return lastModified
  }
  
  def getTags(productId: Long): List[Tag] = DB.withConnection { implicit c =>
    SQL("""
      SELECT Tags.Id, Tags.Name FROM ProductTags
        JOIN Products ON Products.Id = ProductTags.ProductId
        JOIN Tags ON Tags.Id = ProductTags.TagId
        WHERE Products.Id = {productId}
      """
      ).on('productId -> productId).as(Tag.tag *)
  }

  def addTag(productId: Long, tagName: String) = { 
    DB.withConnection { implicit c =>
      val tag = Tag.findOrCreate(tagName)
      SQL("""
        MERGE INTO ProductTags(ProductId, TagId) 
        VALUES ({productId}, {tagId})
      """).on(
        'productId -> productId,
        'tagId -> tag.id).executeUpdate()
    }
  }

  def removeTag(productId: Long, tagName: String) = {
    DB.withConnection { implicit c =>
      for ( tag <- Tag.find(tagName) ) {
        SQL("""
          DELETE FROM ProductTags
          WHERE ProductId = {productId} 
            AND TagId = {tagId}
          """).on(
            'productId -> productId,
            'tagId -> tag.id).executeUpdate()
      }
    }
  }
}
