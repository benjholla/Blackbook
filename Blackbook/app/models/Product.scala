package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Product(id: Long, label: String)

object Product {

  val product = {
    get[Long]("id") ~
      get[String]("label") map {
        case id ~ label => Product(id, label)
      }
  }

  def all(): List[Product] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Products").as(product *)
  }

  def create(label: String) {
    DB.withConnection { implicit c =>
      SQL("INSERT INTO Products(Label) VALUES ({label})").on(
        'label -> label).executeUpdate()
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
      SELECT (Tags.Id, Tags.Label) FROM ProductTags
        JOIN Products.Id = ProductTags.ProductId
        JOIN Tags.Id = ProductTags.TagId
        WHERE Products.Id = {productId}
      """
      ).on('productId -> productId).as(Tag.tag *)
  }
}
