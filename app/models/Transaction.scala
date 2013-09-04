package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import java.util.Date
import scala.language.postfixOps
import java.math.BigDecimal

import util._

case class Transaction(id: Long, 
                       productId: Long, 
                       userName: String, 
                       quantity: Int, 
                       amount: BigDecimal, 
                       notes: String, 
                       cancelled: Boolean) extends traits.Timestamped { 
  
  def getCreateTime(): Date = {
    DB.withConnection { implicit c =>
      val query = SQL("SELECT CreatedAt FROM Transactions WHERE Id = {id}").
        on('id -> id)
      query().map( row => row[Date]("CreatedAt") ).head 
    }
  }

  def getModifyTime(): Date = {
    DB.withConnection { implicit c =>
      val query = SQL("SELECT LastModified FROM Transactions WHERE Id = {id}").
        on('id -> id)
      query().map( row => row[Date]("LastModified") ).head 
    }
  }
}

object Transaction {

  /* Parses a transaction from a SQL result set */
  val transaction = { 
    get[Long]("Transactions.Id") ~ 
    get[Long]("Transactions.ProductId") ~
    get[String]("Transactions.UserName") ~ 
    get[Int]("Transactions.Quantity") ~
    get[BigDecimal]("Transactions.Amount") ~
    get[String]("Transactions.Notes") ~
    get[Boolean]("Transactions.Cancelled") map { 
       case id ~ 
            productId ~ 
            userName ~ 
            quantity ~ 
            amount ~
            notes ~
            cancelled => Transaction(id, productId, userName, quantity, amount, notes, cancelled)
    }
  }

  def all(): List[Transaction] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM Transactions ORDER BY Id DESC").as(transaction *)
  }

  def create(productId: Long, userName: String, quantity: Int, amount: BigDecimal, notes: String): Transaction = { 
    DB.withConnection { implicit c =>
      SQL("INSERT INTO Transactions(ProductId,UserName,Quantity,Amount,Notes) VALUES ({productId}, {userName}, {quantity}, {amount}, {notes})").on(
        'productId -> productId, 'userName -> userName, 'quantity -> quantity, 'amount -> amount, 'notes -> notes).executeUpdate()
      return Transaction(Db.scopeIdentity(), productId, userName, quantity, amount, notes, false)
    }
  }
  
  def update(id: Long,  notes: String) {
    DB.withConnection { implicit c =>
      SQL("UPDATE Transactions SET Notes={notes} WHERE Id={id}").on(
        'notes -> notes, 'id -> id).executeUpdate()
    }
  }
  
  def cancel(id: Long) {
    DB.withConnection { implicit c =>
      SQL("UPDATE Transactions SET Cancelled='yes' WHERE Id={id}").on(
        'id -> id).executeUpdate()
    }
  }

}
