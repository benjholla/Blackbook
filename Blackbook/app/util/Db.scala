package util
import anorm._

object Db { 
  def scopeIdentity()(implicit c: java.sql.Connection): Long = {
    SQL("SELECT SCOPE_IDENTITY()")().map( row =>
      row[Long]("SCOPE_IDENTITY()") ).head
  }
}

