package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Product(id: Long, name: String)

object Product {

  val product = {
    get[Long]("id") ~
      get[String]("name") map {
        case id ~ name => Product(id, name)
      }
  }

  def all(): List[Product] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Products").as(product *)
  }

  def create(name: String) {
    DB.withConnection { implicit c =>
      SQL("INSERT INTO Products(Name) VALUES ({name})").on(
        'name -> name).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withConnection { implicit c =>
      SQL("DELETE FROM PRODUCTS WHERE ID = {id}").on(
        'id -> id).executeUpdate()
    }
  }
  
  def getTags(productId: Long): List[Tag] = DB.withConnection { implicit c =>
    SQL("""
      SELECT Tags.Id AS Id, Tags.Name AS Name FROM ProductTags
        JOIN Products ON Products.Id = ProductTags.ProductId
        JOIN Tags ON Tags.Id = ProductTags.TagId
        WHERE Products.Id = {productId}
      """
      ).on('productId -> productId).as(Tag.tag *)
  }
}
