package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

case class Tag(id: Long, label: String)

object Tag {
  val tag = { 
    get[Long]("id") ~
    get[String]("label") map { 
      case id ~ label => Tag(id, label)
    }
  }

  def normalizeLabel(label: String) = label.toUpperCase

  def all(): List[Tag] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Tags").as(tag *)
  }

  def create(label: String) { 
    DB.withConnection { implicit c =>
      SQL("INSERT INTO Tags(Label) VALUES ({label})").on(
        'label -> normalizeLabel(label)).executeUpdate()
    }
  }

  def getId(label: String) = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Tags WHERE Label = {label}").on(
      'label -> normalizeLabel(label)).as(tag *) 
    match {
      case id :: others => id
      case List() => -1
    }
  }

  def delete(id: Long) { 
    DB.withConnection { implicit c =>
      SQL("DELETE FROM Tags WHERE Id = {id}").on(
        'id -> id).executeUpdate()
    }
  }

  def getProducts(tagId: Long): List[Product] = DB.withConnection { implicit c =>
    SQL("""
      SELECT (Products.Id, Products.Label) FROM ProductTags 
        JOIN Products ON Products.Id = ProductTags.ProductId
        JOIN Tags ON Tags.Id = ProductTags.TagId
        WHERE Tags.Id = {tagId}
      """
      ).on('tagId -> tagId).as(Product.product *)
  }
}
