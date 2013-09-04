package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import java.util.Date
import models._
import traits._

object Users extends Controller with Secured {
  
  val userForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "password" -> nonEmptyText,
      "email" -> email,
      "permissions" -> longNumber(1, Permission.values)
    )
  )
  
  val editUserForm = Form(
    tuple(
      "password" -> nonEmptyText,
      "email" -> email,
      "permissions" -> longNumber(1, Permission.values)
    )
  )

  def users = WithPermissions(Permission.ViewUsers) { implicit request => 
    Ok(views.html.users.index(User.all))
  }

  def viewUser(username: String) = WithPermissions(Permission.ViewUsers) { implicit request =>
    Ok(views.html.users.widgets.view(User.find(username)))
  }

  def editUser(username: String) = WithPermissions(Permission.EditUsers) { implicit request =>
    var user = User.find(username);
    Ok(views.html.users.edit(User.find(username), editUserForm.fill((user.password, user.email, Permission.setToLong(user.getPermissions())))))
  }
  
  def updateUser(username: String) = WithPermissions(Permission.ViewUsers){ implicit request =>
    editUserForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.users.edit(User.find(username), formWithErrors)),
      form => {
        val (password, email, permissions) = form
        User.update(username, password, email, permissions)
        Ok(views.html.users.index(User.all))
      })
  }
  
  def newUser() = WithPermissions(Permission.EditUsers){ implicit request => 
    Ok(views.html.users.newUser(User.all, userForm))
  }
  
  def createUser() = WithPermission(){ implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.users.newUser(User.all, formWithErrors)),
      form => {
        val (name, password, email, permissions) = form
        User.create(name, password, email, permissions)
        Ok(views.html.users.index(User.all))
      })
  }
}
