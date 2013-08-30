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

  def users = WithPermissions(Permission.ViewUsers) { implicit request => 
    Ok(views.html.users.index(User.all))
  }

  def viewUser(name: String) = WithPermissions(Permission.ViewUsers) { request =>
    Ok(views.html.users.view(User.getUser(name)))
  }

  def newUser() = WithPermissions(Permission.EditUsers){ implicit request => 
    Ok(views.html.users.newUser(User.all, userForm))
  }
  
  def createUser() = WithPermission(Permission.EditUsers){ implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.users.newUser(User.all, formWithErrors)),
      form => {
        val (name, password, email, permissions) = form
        User.createUser(name, password, email, permissions)
        Ok(views.html.users.index(User.all))
      })
  }
}
