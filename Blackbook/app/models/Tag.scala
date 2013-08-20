package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

case class Tag(id: Long, name: String)

object Tag {
  val tag = { 
    get[Long]("id") ~
    get[String]("name") map { 
      case id ~ name => Tag(id, name)
    }
  }

  def normalizeName(name: String) = name.toUpperCase

  def all(): List[Tag] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Tags").as(tag *)
  }

  def create(name: String) { 
    DB.withConnection { implicit c =>
      SQL("INSERT INTO Tags(Name) VALUES ({name})").on(
        'name -> normalizeName(name)).executeUpdate()
    }
  }

  def getId(name: String) = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Tags WHERE Name = {name}").on(
      'name -> normalizeName(name)).as(tag *) 
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
      SELECT (Products.Id, Products.Name) FROM ProductTags 
        JOIN Products.Id = ProductTags.ProductId
        JOIN Tags.Id = ProductTags.TagId
        WHERE Tags.Id = {tagId}
      """
      ).on('tagId -> tagId).as(Product.product *)
  }
}
