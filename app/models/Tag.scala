package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import java.util.Date
import scala.language.postfixOps

import util._


case class Tag(id: Long, name: String) extends traits.Timestamped { 
  def getProducts() = { Tag.getProducts(id) }

  def getCreateTime(): Date = {
    DB.withConnection { implicit c =>
      val query = SQL("SELECT CreatedAt FROM Tags WHERE Id = {id}").
        on('id -> id)
      query().map( row => row[Date]("CreatedAt") ).head 
    }
  }

  def getModifyTime(): Date = {
    DB.withConnection { implicit c =>
      val query = SQL("SELECT LastModified FROM Tags WHERE Id = {id}").
        on('id -> id)
      query().map( row => row[Date]("LastModified") ).head 
    }
  }
}

object Tag {

  /* Parses a tag from a SQL result set */
  val tag = { 
    get[Long]("Tags.Id") ~
    get[String]("Tags.Name") map { 
      case id ~ name => Tag(id, name)
    }
  }

  def all(): List[Tag] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Tags").as(tag *)
  }

  def create(name: String): Tag = { 
    DB.withConnection { implicit c =>
      SQL("INSERT INTO Tags(Name) VALUES ({name})").on(
        'name -> Db.normalizeName(name)).executeUpdate()
      return Tag(Db.scopeIdentity(), Db.normalizeName(name))
    }
  }

  def find(name: String): Option[Tag] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Tags WHERE Name = {name}").on(
      'name -> Db.normalizeName(name)).as(tag *).headOption
  }

  def find(id: Long): Option[Tag] = DB.withConnection { implicit c => 
    SQL("SELECT * FROM Tags WHERE Id = {id}").on(
      'id -> id).as(tag *).headOption
  }

  def findOrCreate(name: String): Tag = DB.withConnection { implicit c =>
    find(name) match {
      case Some(tag) => tag
      case _ => create(name)
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
      SELECT Products.* FROM ProductTags 
        JOIN Products ON Products.Id = ProductTags.ProductId
        JOIN Tags ON Tags.Id = ProductTags.TagId
        WHERE Tags.Id = {tagId}
      """
      ).on('tagId -> tagId).as(Product.product *)
  }
}
