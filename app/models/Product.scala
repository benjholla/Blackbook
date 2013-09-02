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

case class Product(
    id: Long, 
    name: String, 
    description: String = null, 
    createTime: Option[Date] = None,
    modifyTime: Option[Date] = None,
    var enabled: Boolean = true
  ) extends traits.Timestamped with traits.Enableable {
  override def equals(v: Any): Boolean = {
    v match { 
        case p: Product => {
          (id == id) &&
          (name == p.name) &&
          (description == p.description) && 
          (getCreateTime() == p.getCreateTime()) &&
          (getModifyTime() == p.getModifyTime())
        }
        case _ => false
    }
  }

  def getTags() = { Product.getTags(id) }
  def addTag(tagName: String) = { Product.addTag(id, tagName) }
  def removeTag(tagName: String) = { Product.removeTag(id, tagName) }

  def getCreateTime() = { 
    createTime.getOrElse( Product.getCreatedAt(id).get )
  }

  def getModifyTime() = { 
    modifyTime.getOrElse( Product.getCreatedAt(id).get )
  }

  def isEnabled() = enabled
  def setEnabled(enabled: Boolean) = {
    DB.withConnection { implicit c =>
      SQL("UPDATE Products SET Enabled = {enabled} WHERE Id = {id}").
        on('enabled -> enabled, 'id -> id).executeUpdate()
    }
    this.enabled = enabled
  }

  def getFiles():List[File] = {
    var files = ArrayBuffer[File]()
    var fileList = new File(Db.uploadsPath() + id + "/files/").listFiles()
    if(fileList != null){
      for(file <- fileList){
        files += file
      }
    }
    return files.toList
  }
}

object Product {

  // Parses a product from a SQL result set
  val product = {
    get[Long]("Products.Id") ~
    get[String]("Products.Name") ~
    get[String]("Products.Description") ~
    get[Option[Date]]("Products.CreatedAt") ~
    get[Option[Date]]("Products.LastModified") ~
    get[Boolean]("Products.Enabled") map (flatten) map ((Product.apply _).tupled)
  }

  def all(includeDisabled: Boolean = false): List[Product] = 
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM Products").as(product *).filter { p => 
        includeDisabled || p.isEnabled 
      }
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
      return Product(id, Db.normalizeName(name), description)
    }
  }
  
  def disable(id: Long) {
    DB.withConnection { implicit c =>
      SQL("UPDATE Products SET Enabled='no' WHERE Id={id}").on(
        'id -> id).executeUpdate()
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
      'name -> name).as(product *).headOption
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
      val tag = Tag.findOrCreate(Db.normalizeName(tagName))
      SQL("""
        INSERT INTO ProductTags(ProductId, TagId) 
        VALUES ({productId}, {tagId})
      """).on(
        'productId -> productId,
        'tagId -> tag.id).executeUpdate()
    }
  }

  def removeTag(productId: Long, tagName: String) = {
    DB.withConnection { implicit c =>
      for (tag <- Tag.find(tagName)) {
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
