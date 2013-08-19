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
    SQL("select * from tags").as(tag *)
  }

  def create(label: String) { 
    DB.withConnection { implicit c =>
      SQL("insert into tags (label) values ({label})").on(
        'label -> normalizeLabel(label)).executeUpdate()
        //normalizeLabel(label)).executeUpdate()
    }
  }

  def getId(label: String) = DB.withConnection { implicit c =>
    SQL("select * from tags where label = {label}").on(
      'label -> normalizeLabel(label)).as(tag *) 
    match {
      case id :: others => id
      case List() => -1
    }
  }

  def delete(id: Long) { 
    DB.withConnection { implicit c =>
      SQL("delete from tags where id = {id}").on(
        'id -> id).executeUpdate()
    }
  }
}
