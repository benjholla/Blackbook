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
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "email" -> email,
      "permissions" -> longNumber(1, Permission.values),
      "enabled" -> boolean
    )(User.DbUser.apply)(User.DbUser.unapply)
  )

  def users = WithPermissions(Permission.ViewUsers) { 
    implicit request => Ok(views.html.users.index(User.all))
  }

  def user(name: String) = WithPermissions(Permission.ViewUsers) 
  { request =>
    Ok(views.html.users.widgets.view_user(User.getUser(name)))
  }

  def newUser() = WithPermissions(Permission.EditUsers)
  { request => 
    Ok(views.html.users.widgets.new_user(userForm))
  }
}
