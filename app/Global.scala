import controllers._
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import util.ExtendedHeaders._

object Global extends GlobalSettings { 
  override def onHandlerNotFound(request: RequestHeader) = { 
    controllers.Application.notFound(request)
  }
}

