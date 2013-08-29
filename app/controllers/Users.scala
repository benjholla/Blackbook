package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import java.util.Date

import models._
import traits._

object Users extends Controller with Secured {
  def users = WithPermissions(Permission.ViewUsers) { 
    implicit request => Ok(views.html.users.index(User.all))
  }

  def user(name: String) = WithPermissions(Permission.ViewUsers) 
  { request =>
    Ok(views.html.users.widgets.view_user(User.getUser(name)))
  }
}
