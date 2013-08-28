package util

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import scala.language.implicitConversions

class ExtendedHeaders(rh: RequestHeader) {
  def referer = {
    rh.headers.get("REFERER") getOrElse controllers.routes.Decoy.home().url
  }
}

object ExtendedHeaders {
  implicit def extendedHeaders(rh: RequestHeader) = new ExtendedHeaders(rh)
}
