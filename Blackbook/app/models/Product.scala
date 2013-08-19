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
    SQL("select * from product").as(product *)
  }

  def create(label: String) {
    DB.withConnection { implicit c =>
      SQL("insert into product (label) values ({label})").on(
        'label -> label).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withConnection { implicit c =>
      SQL("delete from product where id = {id}").on(
        'id -> id).executeUpdate()
    }
  }

}