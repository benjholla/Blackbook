package controllers

import java.io.File
import models._
import models.{Permission => Perm}
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import scala.collection.mutable.ArrayBuffer
import traits._
import util.Db
import play.api.data.format.Formatter

object Transactions extends Controller with Secured {

  val transactionForm = Form (
    tuple("productId" -> number,
          "userName" -> nonEmptyText,
          "quantity" -> number,
         "amount" -> Forms.bigDecimal,
         "notes" -> nonEmptyText
         )
  )
  
  def transactions() = WithPermissions() { implicit request => 
    Ok(views.html.transactions.index(Transaction.all()))
  }
  
  def newTransaction() = WithPermissions(Perm.ViewLedger + Perm.EditLedger) { implicit request =>
    Ok(views.html.transactions.newTransaction(transactionForm))
  }
  
  def createTransaction() = WithPermissions(Perm.ViewLedger + Perm.EditLedger) { implicit request =>
    transactionForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.transactions.newTransaction(formWithErrors)),
      form => {
        val (productId, userName, quantity, amount, notes) = form
        Transaction.create(productId, userName, quantity, new java.math.BigDecimal(amount.toString()), notes)
        Redirect(routes.Transactions.transactions())
      })
  }
    
}
