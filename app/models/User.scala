package models

import anorm.SqlParser._
import anorm._
import controllers.Assets
import java.io.File
import java.util.Date
import play.api.Play.current
import play.api.db._
import scala.language.implicitConversions
import scala.language.postfixOps
import scala.collection.immutable.BitSet
import util.Db
import scala.language.implicitConversions

object Permission extends Enumeration {
  val ViewProducts = Value(1)
  val EditProducts = Value(2)
  val ViewLedger = Value(4)
  val EditLedger = Value(8)
  val ViewUsers = Value(16)
  val EditUsers = Value(32)
  val DownloadFile = Value(64)
  val MakeAnnouncements = Value(128)

  type Set = Permission.ValueSet
  def Set() = Permission.ValueSet()

  implicit def wrapValue(p: Permission.Value): Permission.Set = { 
    ValueSet(p)
  }

  implicit def setToLong(ps: Permission.Set): Long = ps.foldLeft(0) { (l, r) =>
    l | r.id
  }
}

object User {
  trait User {
    def name(): String
    def email(): String = ""

    def password(): String
    def enabled(): Boolean

    def hasPermission(perm: Permission.Value): Boolean
    def hasPermissions(perms: Permission.Set): Boolean =
      perms.forall(hasPermission)
    def hasSomePermission(perms: Permission.Set): Boolean = 
      perms.exists(hasPermission)
    def hasRole(role: Permission.Set): Boolean = 
      hasPermissions(role)

    def getPermissions(): Permission.Set =
      Permission.values filter hasPermission

    def anyPermissions(): Boolean = 
      getPermissions != Permission.Set.empty

    def authenticate(token: String): Boolean = token == password

    def update(u: User): Unit = {}
  }

  case class TestUser() extends User {
    def name() = "test"
    def password() = "secret"
    def enabled() = true
    def hasPermission(perm: Permission.Value) = true
  }

  case class NullUser() extends User { 
    def name() = ""
    def password() = ""
    def enabled() = false
    def hasPermission(prem: Permission.Value) = false
  }

  case class DbUser
    ( username: String,
      password: String,
      override val email: String,
      permissions: Long,
      enabled: Boolean
    ) extends User {
    def name() = username
    def hasPermission(perm: Permission.Value) = ((permissions & perm.id) != 0)
    override def authenticate(password: String): Boolean = {
      DB.withConnection { implicit c => 
        val query = SQL("""
          UPDATE Users SET LastLogin = now() 
          WHERE Name = {name}
          AND Password = {password}
        """).on('name -> username, 'password -> password)

        return query.executeUpdate() > 0
      }
    }
  }

  // Parses a DB user from a SQL result set.
  val dbUser = {
    get[String]("Users.Name") ~
    get[String]("Users.Password") ~
    get[String]("Users.Email") ~
    get[Long]("Users.Permissions") ~
    get[Boolean]("Users.Enabled") map (flatten) map ((DbUser.apply _).tupled)
  }

  private def localUser(username: String): User = {
    if (username == "test") TestUser()
    else NullUser()
  }

  def create(username:String, password:String, email:String, permissions:Long) = {
    DB.withConnection { implicit c =>
      SQL("INSERT INTO Users(Name,Password,Email,Permissions) VALUES ({name},{password},{email},{permissions})").on(
        'name -> Db.normalizeName(username), 'password -> password, 'email -> email, 'permissions -> permissions).executeUpdate()
    }
  }
  
  def update(username:String, password:String, email:String, permissions:Long) = {
    DB.withConnection { implicit c =>
      SQL("UPDATE Users SET Password={password}, Email={email}, Permissions={permissions} WHERE Name={username}").on(
        'username -> username, 'password -> password, 'email -> email, 'permissions -> permissions).executeUpdate()
    }
  }
  
  def disable(username:String) = {
    DB.withConnection { implicit c =>
      SQL("UPDATE Users SET Enabled='no' WHERE Name={username}").on(
        'username -> username).executeUpdate()
    }
  }
  
  def find(username: String): User = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM Users WHERE Name = {name}").
        on('name -> username).as(dbUser *)
    }.headOption getOrElse localUser(username)
  }

  def all(): List[User] = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM Users").as(dbUser *)
    }  
  }

  def authenticate(username: String, password: String): User = {
    val user = find(username)
    if (user.authenticate(password)) user
    else NullUser()
  }
}

