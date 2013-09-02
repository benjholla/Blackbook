package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import scala.language.postfixOps
import util.Db
import java.util.Date
import java.io.File
import scala.collection.mutable.ArrayBuffer
import controllers.Assets

case class Announcement(
    id: Long, 
    message: String, 
    createTime: Option[Date] = None,
    modifyTime: Option[Date] = None,
    var enabled: Boolean = true
  ) extends traits.Timestamped with traits.Enableable {

  def getCreateTime() = { 
    createTime.getOrElse( Announcement.getCreatedAt(id).get )
  }

  def getModifyTime() = { 
    modifyTime.getOrElse( Announcement.getCreatedAt(id).get )
  }

  def isEnabled() = enabled
  def setEnabled(enabled: Boolean) = {
    DB.withConnection { implicit c =>
      SQL("UPDATE Announcements SET Enabled = {enabled} WHERE Id = {id}").
        on('enabled -> enabled, 'id -> id).executeUpdate()
    }
    this.enabled = enabled
  }

}

object Announcement {

  // Parses a announcement from a SQL result set
  val announcement = {
    get[Long]("Announcements.Id") ~
    get[String]("Announcements.Message") ~
    get[Option[Date]]("Announcements.CreatedAt") ~
    get[Option[Date]]("Announcements.LastModified") ~
    get[Boolean]("Announcements.Enabled") map (flatten) map ((Announcement.apply _).tupled)
  }

  def all(includeDisabled: Boolean = false): List[Announcement] = 
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM Announcements ORDER BY Id DESC").as(announcement *).filter { a => 
        includeDisabled || a.isEnabled 
      }
    }

  def create(message: String): Announcement = {
    DB.withConnection { implicit c =>
      SQL("INSERT INTO Announcements(Message) VALUES ({message})").on(
        'message -> message).executeUpdate()
      return Announcement(Db.scopeIdentity(), message)
    }
  }
  
  def update(id: Long, message:String): Announcement = {
    DB.withConnection { implicit c =>
      SQL("UPDATE Announcements SET Message={message} WHERE Id={id}").on(
        'message -> message, 'id -> id).executeUpdate()
      return Announcement(id, message)
    }
  }
  
  def disable(id: Long) {
    DB.withConnection { implicit c =>
      SQL("UPDATE Announcements SET Enabled='no' WHERE Id={id}").on(
        'id -> id).executeUpdate()
    }
  }

  def getCreatedAt(id: Long): Option[Date] = DB.withConnection { implicit c =>
    val selectCreatedAt = SQL("SELECT CreatedAt FROM Announcements WHERE Id = {id}").on('id -> id)
    val createdAt = selectCreatedAt().map(row => row[Date]("CreatedAt")).headOption
    return createdAt
  }
  
  def getLastModified(id: Long): Option[Date] = DB.withConnection { implicit c =>
    val selectLastModified = SQL("SELECT LastModified FROM Announcements WHERE Id = {id}").on('id -> id)
    val lastModified = selectLastModified().map(row => row[Date]("LastModified")).headOption
    return lastModified
  }
  
}
