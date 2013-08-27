package util
import anorm._

object Db { 
  
  /**
   * Returns true if name normalizes to a valid string
   */
  def isValidName(name:String):Boolean = {
    normalizeName(name).length() > 0
  }
  
  /**
   * Makes name lower case, replaces invalid characters with an underscore
   */
  def normalizeName(name:String):String = {
    name.trim().replaceAll("[^A-Za-z0-9_.]+", "_").toLowerCase()
  }
  
  def scopeIdentity()(implicit c: java.sql.Connection): Long = {
    val query = SQL("SELECT LastVal()")
    query().map( row => row[Long]("LastVal") ).head
  }
  
  def uploadsPath():String = {
    "./uploads/products/"
  }
  
  def defaultProductIconPath():String = {
    "./public/images/default-product-icon.png"
  }
}
