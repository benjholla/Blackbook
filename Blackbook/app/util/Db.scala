package util
import anorm._

object Db { 
  
  /**
   * Returns true if the the name is normalized
   * Matches true if the input is a word character or whitespace
   */
  def isValidName(name:String):Boolean = {
    name.trim().matches(raw"[\w\s]+")
  }
  
  /**
   * Normalize a name for insertion into the database
   * This function replaces whitespace with an underscore and make lowercase
   */
  def normalizeName(name:String):String = {
    name.replaceAll(raw"[\s_]+", "_").toLowerCase()
  }
  
  def scopeIdentity()(implicit c: java.sql.Connection): Long = {
    val query = SQL("SELECT SCOPE_IDENTITY()")
    query().map( row => row[Long]("SCOPE_IDENTITY()") ).head
  }
}
